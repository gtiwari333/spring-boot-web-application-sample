package gt.app.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "comment")
@Data
public class Comment extends BaseAuditingEntity {
    private String content;

    private Status status = Status.ACTIVE; //temporary

    @JoinColumn(nullable = false)
    private Long articleId;

    @JoinColumn(nullable = true)
    private Long parentCommentId;

    enum Status {
        AWAITING_APPROVAL, REJECTED, ACTIVE, DELETED
    }

    public Comment() {
    }

    public Comment(String content, Article article) {
        this.content = content;
        this.articleId = article.id;
    }

    public void addChildComment(Comment childComment) {
        childComment.setParentCommentId(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
