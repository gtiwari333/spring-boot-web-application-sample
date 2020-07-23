package gt.app.domain;

import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "comment")
@Data
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Comment extends BaseAuditingEntity {
    private String content;

    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.SHOWING; //temporary

    @JoinColumn(nullable = false)
    private Long articleId;

    @JoinColumn(nullable = true)
    private Long parentCommentId;

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
