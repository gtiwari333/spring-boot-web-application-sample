package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.exception.RecordNotFoundException;
import gt.app.modules.review.ContentCheckRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    final ArticleRepository articleRepository;
    final CommentRepository commentRepository;
    final ContentCheckRequestService contentCheckRequestService;

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @CacheEvict(cacheNames = {"articleRead"}, key = "#c.articleId")
    public Comment save(Comment c) {
        return commentRepository.save(c);
    }

    @CacheEvict(cacheNames = {"articleRead"}, key = "#c.articleId")
    public void save(NewCommentDto c) {
        Comment comment = new Comment(c.content, c.articleId);

        if (c.parentCommentId != null && c.parentCommentId > 0) {
            //checks if the requested parentCommentId actually belong to the articleId in request
            if (!commentRepository.existsByIdAndArticleId(c.parentCommentId, c.articleId)) {
                throw new RecordNotFoundException("The given parent comment id " + c.parentCommentId + " doesn't belong to article" + c.articleId);
            }
            comment.setParentCommentId(c.parentCommentId);
        }

        save(comment);

        contentCheckRequestService.sendForAutoContentReview(comment);
    }


    public List<ArticleReadDto.CommentDto> readComments(Long articleId) {
        return commentRepository.findAllByArticleId(articleId).stream().map(ArticleMapper.INSTANCE::map).collect(Collectors.toList());
    }

}
