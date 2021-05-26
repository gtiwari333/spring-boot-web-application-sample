package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    final ArticleRepository articleRepository;
    final CommentRepository commentRepository;
    final ProfanityChecker profanityChecker;

    public void save(Comment c) {
        commentRepository.save(c);
    }

    public void save(NewCommentDto com) {
        Comment comment = new Comment(com.content, com.articleId);

        if (com.parentCommentId != null && com.parentCommentId > 0) {
            //checks if the requested parentCommentId actually belong to the articleId in request
            if (!commentRepository.existsByIdAndArticleId(com.parentCommentId, com.articleId)) {
                throw new RecordNotFoundException("The given parent comment id " + com.parentCommentId + " doesn't belong to article" + com.articleId);
            }
            comment.setParentCommentId(com.parentCommentId);
        }

        save(comment);

        profanityChecker.handleProfanityCheck(comment);
    }


    public List<ArticleReadDto.CommentDto> readComments(Long articleId) {
        return commentRepository.findAllByArticleId(articleId).stream().map(ArticleMapper.INSTANCE::map).collect(Collectors.toList());
    }

}
