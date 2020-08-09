package gt.app.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "G_RECEIVED_FILE")
@NoArgsConstructor
public class ReceivedFile {

    @Id
    @Type(type = "uuid-char")
    UUID id;

    Instant receivedDate;
    String originalFileName;

    String storedName;

    @Enumerated(EnumType.STRING)
    FileGroup fileGroup;

    public ReceivedFile(FileGroup group, String originalFileName, String storedName) {
        this.fileGroup = group;
        this.originalFileName = originalFileName;
        this.storedName = storedName;
        this.id = UUID.randomUUID();
    }

    public enum FileGroup {
        NOTE_ATTACHMENT,
        //add other
    }
}
