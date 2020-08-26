package gt.app.modules.article;

import lombok.Data;

@Data
public class NewCommentDto {
    String content;
    Long articleId;
    Long parentCommentId;
}
