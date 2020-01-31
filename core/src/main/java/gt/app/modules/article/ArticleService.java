package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import gt.app.domain.ReceivedFile;
import gt.app.modules.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private static final ReceivedFile.FileGroup FILE_GROUP = ReceivedFile.FileGroup.NOTE_ATTACHMENT;
    private final ArticleRepository articleRepository;
    private final FileService fileService;

    public Article save(Article article) {
        return articleRepository.save(article);
    }

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

        return save(article);
    }

    public Article update(ArticleEditDto dto) {

        Optional<Article> articleOpt = articleRepository.findWithFilesAndUserById(dto.getId());
        return articleOpt.map(article -> {
                ArticleMapper.INSTANCE.createToEntity(dto, article);
                return save(article);
            }
        ).orElseThrow();
    }

    public ArticleReadDto read(Long id) {
        return articleRepository.findWithFilesAndUserByIdAndStatus(id, ArticleStatus.PUBLISHED)
            .map(ArticleMapper.INSTANCE::mapForRead)
            .orElseThrow();
    }

    public Page<ArticleReadDto> readAll(Pageable pageable) {
        return articleRepository.findWithFilesAndUserAllByStatus(pageable, ArticleStatus.PUBLISHED)
            .map(ArticleMapper.INSTANCE::mapForRead);
    }

    public Page<ArticleReadDto> readAllByUser(Pageable pageable, Long userId) {
        return articleRepository.findWithFilesAndUserByCreatedByUser_IdAndStatusOrderByCreatedDateDesc(pageable, userId, ArticleStatus.PUBLISHED)
            .map(ArticleMapper.INSTANCE::mapForRead);
    }

    public ArticleReadDto readForReview(Long id) {
        return articleRepository.findWithFilesAndUserByIdAndStatus(id, ArticleStatus.FLAGGED)
            .map(ArticleMapper.INSTANCE::mapForRead)
            .orElseThrow();
    }

    public Page<ArticleReadDto> getAllToReview(Pageable pageable) {
        return articleRepository.findWithFilesAndUserAllByStatus(pageable, ArticleStatus.FLAGGED)
            .map(ArticleMapper.INSTANCE::mapForRead);
    }

    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    public Long findCreatedByUserIdById(Long id) {
        return articleRepository.findCreatedByUserIdById(id);
    }


    public Optional<Article> handleReview(ArticleReviewDto dto) {
        return articleRepository.findWithFilesAndUserByIdAndStatus(dto.getId(), ArticleStatus.FLAGGED)
            .map(n -> {
                n.setStatus(dto.getVerdict());
                return save(n);
            });
    }
}
