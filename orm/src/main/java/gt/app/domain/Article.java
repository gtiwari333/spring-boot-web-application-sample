package gt.app.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Article extends BaseAuditingEntity implements Serializable {

    @NotEmpty
    private String title;

    @NotEmpty
    @Lob //postgres saves as 'text', must use select lo_get(content::oid) from article; to retrieve
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.PUBLISHED;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<ReceivedFile> attachedFiles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "articleId")
    private Set<Comment> comments = new HashSet<>();


}
