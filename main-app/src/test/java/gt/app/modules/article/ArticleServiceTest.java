package gt.app.modules.article;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleServiceTest {

    @Test
    void testMapNested() {

        /*

        1, Good Article, NULL
        2, Thanks, 1
        3, Welcome!, 2
        4, :), 3
        5, Great Post, 1
        6, Thanks Again, 5
        7, Great Article, NULL

        Convert to >>

        First Article:
           Comments;
            - 1Good Article
                - 2Thanks
                    - 3Welcome
                            - 4:)
                - 5Great Post
                    - 6Thanks Again
             -7Great Article
         */

        ArticleReadDto flat = getTestArticle();
        var service = new ArticleService(null, null, null);

        ArticleReadDto nested = service.mapNested(flat);

        assertThat(flat.getId()).isEqualTo(nested.getId());
        assertThat(flat.getTitle()).isEqualTo(nested.getTitle());
        assertThat(flat.getContent()).isEqualTo(nested.getContent());

        //test nested comments
        assertThat(nested.getComments().size()).isEqualTo(2);
        assertThat(nested.getComments().get(0).getId()).isEqualTo(flat.getComments().get(0).id);
        assertThat(nested.getComments().get(0).getChildComments().size()).isEqualTo(2);
        assertThat(nested.getComments().get(0).getChildComments().get(0).getChildComments().size()).isEqualTo(1);
        assertThat(nested.getComments().get(0).getChildComments().get(0).getChildComments().get(0).getId()).isEqualTo(3);
        assertThat(nested.getComments().get(0).getChildComments().get(0).getChildComments().get(0).getChildComments().size()).isEqualTo(1);
        assertThat(nested.getComments().get(0).getChildComments().get(0).getChildComments().get(0).getChildComments().get(0).getId()).isEqualTo(4);
        assertThat(nested.getComments().get(0).getChildComments().get(1).getChildComments().size()).isEqualTo(1);

        assertThat(nested.getComments().get(1).getId()).isEqualTo(flat.getComments().get(6).id);


    }

    private ArticleReadDto getTestArticle() {
        ArticleReadDto a1 = getArticle("First Article", "The content of First Article");

        //comments on a1
        ArticleReadDto.CommentDto a1c1 = getComment(a1, 1L, "Good Article");
        ArticleReadDto.CommentDto a1c1_1 = getChildComment(a1, 2L, "Thanks", a1c1);
        ArticleReadDto.CommentDto a1c1_1_1 = getChildComment(a1, 3L, "Welcome!", a1c1_1);
        ArticleReadDto.CommentDto a1c1_1_1_1 = getChildComment(a1, 4L, ":)", a1c1_1_1);
        ArticleReadDto.CommentDto a1c1_2 = getChildComment(a1, 5L, "Great Post", a1c1);
        ArticleReadDto.CommentDto a1c1_2_1 = getChildComment(a1, 6L, "Thanks Again", a1c1_2);
        ArticleReadDto.CommentDto a1c2 = getComment(a1, 7L, "Great Article");

        return a1;
    }


    private ArticleReadDto.CommentDto getChildComment(ArticleReadDto a, Long commentId, String content, ArticleReadDto.CommentDto parentComment) {
        ArticleReadDto.CommentDto c = getComment(a, commentId, content);

        c.setParentCommentId(parentComment.id);

        return c;
    }

    private ArticleReadDto.CommentDto getComment(ArticleReadDto a, Long id, String content) {
        var c = new ArticleReadDto.CommentDto();
        c.setContent(content);
        c.setArticleId(a.getId());
        c.setId(id);

        a.getComments().add(c);

        return c;
    }

    private ArticleReadDto getArticle(String title, String content) {
        ArticleReadDto article = new ArticleReadDto();
        article.setTitle(title);
        article.setContent(content);
        return article;
    }
}
