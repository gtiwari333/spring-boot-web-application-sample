package gt.app.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "article")
@Data
public class Article extends BaseAuditingEntity {

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.PUBLISHED;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<ReceivedFile> attachedFiles = new ArrayList<>();

}
