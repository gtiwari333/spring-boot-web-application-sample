package gt.mail.modules.email;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueuedEmailRecipient implements Serializable {

    private static final long serialVersionUID = -4856661516918265380L;

    private Collection<EmailAddress> tos = new HashSet<>();
    private Collection<EmailAddress> ccs = new HashSet<>();
    private Collection<EmailAddress> bccs = new HashSet<>();
}


