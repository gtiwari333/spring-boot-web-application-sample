package gt.app.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "note")
@Data
public class Note extends BaseAuditingEntity {

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NoteStatus status = NoteStatus.PUBLISHED;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<ReceivedFile> attachedFiles = new ArrayList<>();

}
