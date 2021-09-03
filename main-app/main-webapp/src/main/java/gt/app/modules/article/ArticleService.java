package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import gt.app.domain.ReceivedFile;
import gt.app.modules.file.FileService;
import gt.app.modules.review.ContentCheckRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private static final ReceivedFile.FileGroup FILE_GROUP = ReceivedFile.FileGroup.NOTE_ATTACHMENT;
    private final ArticleRepository articleRepository;
    private final FileService fileService;
    private final JmsTemplate jmsTemplate;
    private final CommentRepository commentRepo;
    private final ContentCheckRequestService contentCheckRequestService;

    @Caching(
        evict = {
            @CacheEvict(cacheNames = {"articleForReview"}, key = "#article.id"),
            @CacheEvict(cacheNames = {"previewForPublicHomePage", "previewAllWithFilesByUser", "getAllToReview"}, allEntries = true)
        }
    )
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    @Caching(
        evict = {
            @CacheEvict(cacheNames = {"articleForReview"}, key = "#result.id"),
            @CacheEvict(cacheNames = {"previewForPublicHomePage", "previewAllWithFilesByUser", "getAllToReview"}, allEntries = true)
        }
    )
    public Article createArticle(ArticleCreateDto dto) {

        List<ReceivedFile> files = new ArrayList<>();
        for (MultipartFile mpf : dto.getFiles()) {

            if (mpf.isEmpty()) {
                continue;
            }

            String fileId = fileService.store(FILE_GROUP, mpf);
            files.add(new ReceivedFile(FILE_GROUP, mpf.getOriginalFilename(), fileId));
        }

        Article article = ArticleMapper.INSTANCE.createToEntity(dto);
        article.getAttachedFiles().addAll(files);
        article.setStatus(ArticleStatus.UNDER_AUTO_REVIEW);

        save(article);

        contentCheckRequestService.sendForAutoContentReview(article);

        return article;
    }

    @Caching(
        evict = {
            @CacheEvict(cacheNames = {"articleForReview"}, key = "#result.id"),
            @CacheEvict(cacheNames = {"previewForPublicHomePage", "previewAllWithFilesByUser", "getAllToReview"}, allEntries = true)
        }
    )
    public Article update(ArticleEditDto dto) {

        Optional<Article> articleOpt = articleRepository.findWithFilesAndUserById(dto.getId());
        return articleOpt.map(article -> {
                ArticleMapper.INSTANCE.createToEntity(dto, article);
                return save(article);
            }
        ).orElseThrow();
    }

    @Cacheable(cacheNames = "articleRead", key = "#id")
    public ArticleReadDto read(Long id) {
        //TODO: filter out unpublished comments - write a jooq or querydsl query
        ArticleReadDto dto = articleRepository.findOneWithAllByIdAndStatus(id, ArticleStatus.PUBLISHED, Sort.by(Sort.Direction.DESC, "id"))
            .map(ArticleMapper.INSTANCE::mapForRead)
            .map(this::mapNested)
            .orElseThrow();

        jmsTemplate.convertAndSend("article-read", ArticleMapper.INSTANCE.mapForPublishedEvent(dto));

        return dto;
    }

    protected ArticleReadDto mapNested(ArticleReadDto s) {
        ArticleReadDto d = new ArticleReadDto();
        BeanUtils.copyProperties(s, d, "comments");

        for (ArticleReadDto.CommentDto c : s.getComments()) {

            if (c.parentCommentId == null) {
                d.getComments().add(c);
            } else {
                ArticleReadDto.CommentDto parent = findParentWithId(s, c.parentCommentId);
                parent.getChildComments().add(c);
            }
        }

        return d;
    }

    private ArticleReadDto.CommentDto findParentWithId(ArticleReadDto d, Long parentCommentId) {
        return d.getComments().stream()
            .filter(c -> c.id.equals(parentCommentId))
            .findFirst().orElseThrow();
    }

    @Cacheable(cacheNames = "previewForPublicHomePage")
    public Page<ArticlePreviewDto> previewForPublicHomePage(Pageable pageable) {
        return articleRepository.findWithUserAndAttachedFilesByStatus(ArticleStatus.PUBLISHED, pageable)
            .map(ArticleMapper.INSTANCE::mapForPreviewListing);
    }

    @Cacheable(cacheNames = "previewAllWithFilesByUser")
    public Page<ArticlePreviewDto> previewAllWithFilesByUser(Pageable pageable, UUID userId) {
        return articleRepository.findWithFilesAndUserByCreatedByUser_IdAndStatusOrderByCreatedDateDesc(userId, ArticleStatus.PUBLISHED, pageable)
            .map(ArticleMapper.INSTANCE::mapForPreviewListing);
    }

    @Cacheable(cacheNames = "articleForReview", key = "#id")
    public ArticlePreviewDto readForReview(Long id) {
        return articleRepository.findOneWithUserAndAttachedFilesByIdAndStatus(id, ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW)
            .map(ArticleMapper.INSTANCE::mapForReview)
            .orElseThrow();
    }

    public Page<ArticlePreviewDto> getAllToReview(Pageable pageable) {
        return articleRepository.findWithUserAndAttachedFilesByStatus(ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW, pageable)
            .map(ArticleMapper.INSTANCE::mapForPreviewListing);
    }

    @Transactional
    @Caching(
        evict = {
            @CacheEvict(cacheNames = {"articleForReview"}, key = "#id"),
            @CacheEvict(cacheNames = {"previewForPublicHomePage", "previewAllWithFilesByUser", "getAllToReview"}, allEntries = true)
        }
    )
    public void delete(Long id) {
        commentRepo.deleteByArticleId(id);
        articleRepository.deleteById(id);
    }

    @Cacheable(cacheNames = {"article-findCreatedByUserIdById"})
    public UUID findCreatedByUserIdById(Long articleId) {
        return articleRepository.findCreatedByUserIdById(articleId);
    }

    @Caching(
        evict = {
            @CacheEvict(cacheNames = {"articleForReview"}, key = "#dto.id"),
            @CacheEvict(cacheNames = {"previewForPublicHomePage", "previewAllWithFilesByUser", "getAllToReview"}, allEntries = true)
        }
    )
    public Optional<Article> handleReview(ArticleReviewResultDto dto) {
        return articleRepository.findByIdAndStatus(dto.getId(), ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW)
            .map(n -> {
                n.setStatus(dto.getVerdict());
                return save(n);
            });
    }

    public void testCountStatuses() {
        log.info("Size of flagged articles {}", articleRepository.findArticles(ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW).size());
        log.info("Size of flagged articles {}", articleRepository.countArticles(ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW));
    }

    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    public Optional<Article> findOneWithUserById(Long id) {
        return articleRepository.findOneWithUserById(id);
    }
}
