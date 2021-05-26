package gt.app.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
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
        NOTE_ATTACHMENT("attachments"),
        ;
        //add other
        public final String path;

        FileGroup(String path) {
            this.path = path;

        }
    }
}
