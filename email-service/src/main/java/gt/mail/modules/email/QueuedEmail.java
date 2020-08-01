package gt.mail.modules.email;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "queued_email")
@Data
@TypeDef(
    name = "json",
    typeClass = JsonStringType.class,
    defaultForType = JsonNode.class
)
public class QueuedEmail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Column(nullable = false)
    private Instant createdDate = Instant.now();

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @NotNull
    @Column(nullable = false)
    private String subject;

    @NotNull
    @Type(type = "json")
    @Column(name = "from_address", columnDefinition = "LONGTEXT")
    private EmailAddress from = new EmailAddress();

    @Type(type = "json")
    @Column(name = "recipient", columnDefinition = "LONGTEXT")
    private QueuedEmailRecipient recipient = new QueuedEmailRecipient();

    @NotNull
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @OneToMany(mappedBy = "queuedEmail")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JsonManagedReference
    private List<QueuedEmailAttachment> attachments = new ArrayList<>();

    private Instant sentDate;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.QUEUED;

    private int retryCount;

    private Instant lastTryDate;

    @Column(columnDefinition = "LONGTEXT")
    private String lastFailReason;

    public void addAttachment(QueuedEmailAttachment attachment) {
        attachments.add(attachment);
        attachment.setQueuedEmail(this);
    }

    @Override
    public String toString() {
        return "QueuedEmail{" +
            "id='" + getId() + '\'' +
            "subject='" + subject + '\'' +
            '}';
    }

    public enum Status {
        QUEUED, FAILED, SENT
    }

    public enum ContentType {
        PLAIN, HTML
    }
}
