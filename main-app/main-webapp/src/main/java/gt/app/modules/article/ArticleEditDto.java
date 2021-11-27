package gt.app.modules.article;

import lombok.Data;

@Data
public class ArticleEditDto {

    private Long id;
    private String title;
    private String content;
    private String[] tags = {};
}
