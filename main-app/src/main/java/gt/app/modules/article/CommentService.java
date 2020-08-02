package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    final ArticleRepository articleRepository;
    final CommentRepository commentRepository;

    public void addToArticle(NewCommentDto com, Long articleId) {
        Article article = articleRepository.getOne(articleId);
        Comment comment = new Comment(com.content, article);

        commentRepository.save(comment);
    }

    public void addChildComment(Long parentCommentId, NewCommentDto com, Long articleId) {
        Article article = articleRepository.getOne(articleId);
        Comment parentComment = commentRepository.getOne(parentCommentId);

        parentComment.addChildComment(new Comment(com.content, article));

        commentRepository.save(parentComment);
    }


    public List<ArticleReadDto.CommentDto> readComments(Long articleId) {
        return commentRepository.findAllByArticleId(articleId).stream().map(ArticleMapper.INSTANCE::map).collect(Collectors.toList());
    }


}
