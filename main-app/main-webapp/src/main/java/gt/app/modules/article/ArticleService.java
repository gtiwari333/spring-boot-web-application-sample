package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import gt.app.domain.ReceivedFile;
import gt.app.domain.Tag;
import gt.app.modules.file.FileService;
import gt.app.modules.review.ContentCheckService;
import gt.app.modules.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private static final ReceivedFile.FileGroup FILE_GROUP = ReceivedFile.FileGroup.NOTE_ATTACHMENT;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final FileService fileService;
    private final JmsTemplate jmsTemplate;
    private final CommentRepository commentRepo;
    private final ContentCheckService contentCheckService;

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

        cudTags(article, dto.getTags());

        articleRepository.save(article);

        contentCheckService.sendForAutoContentReview(article);

        return article;
    }

    public Article update(ArticleEditDto dto) {

        Optional<Article> articleOpt = articleRepository.findWithFilesAndUserById(dto.getId());
        return articleOpt.map(article -> {
                ArticleMapper.INSTANCE.createToEntity(dto, article);

                article.setLastModifiedDate(Instant.now()); //update article even when the child records are updated
                article.getTags().clear();
                cudTags(article, dto.getTags());

                return articleRepository.save(article);
            }
        ).orElseThrow();
    }

    public void cudTags(Article article, String[] tags) {
        Set<Tag> tagEntities = new HashSet<>();
        for (String t : tags) {
            Tag tag;
            if (!tagRepository.existsByName(t)) {
                tag = tagRepository.save(new Tag(t));
            } else {
                tag = tagRepository.getById(t);
            }
            tagEntities.add(tag);
        }
        article.setTags(tagEntities);
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

    @Cacheable(cacheNames = "previewForPublicHomePageByTag")
    public Page<ArticlePreviewDto> previewForPublicHomePageByTag(Pageable pageable, String tag) {
        return articleRepository.findWithUserAndAttachedFilesByStatusAndTagsName(ArticleStatus.PUBLISHED, tag, pageable)
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
    public void delete(Long id) {
        commentRepo.deleteByArticleId(id);
        articleRepository.deleteById(id);
    }

    @Cacheable(cacheNames = {"article-findCreatedByUserIdById"})
    public UUID findCreatedByUserIdById(Long articleId) {
        return articleRepository.findCreatedByUserIdById(articleId);
    }

    public Optional<Article> handleReview(ArticleReviewResultDto dto) {
        return articleRepository.findByIdAndStatus(dto.getId(), ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW)
            .map(n -> {
                n.setStatus(dto.getVerdict());
                return articleRepository.save(n);
            });
    }

    public void testCountStatuses() {
        log.info("Size of flagged articles {}", articleRepository.findArticles(ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW).size());
        log.info("Size of flagged articles {}", articleRepository.countArticles(ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW));
    }

}
