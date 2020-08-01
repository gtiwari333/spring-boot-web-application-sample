package gt.mail.modules.email;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "queued_email_attachment")
@Data
public class QueuedEmailAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    @JoinColumn(name = "queued_email_id", nullable = false, updatable = false)
    @JsonBackReference
    private QueuedEmail queuedEmail;

    private String fileName;
    private String savedFileName;

    @Override
    public String toString() {
        return "QueuedEmailAttachment{" +
            "fileName=" + fileName +
            '}';
    }
}
