package gt.api.email;

import java.util.Collection;
import java.util.List;

public record EmailDto(String fromEmail, String fromName, Collection<String> to, Collection<String> cc,
                       Collection<String> bcc,
                       String subject, String content, boolean isHtml, FileBArray[] files) {

    public static EmailDto of(String fromEmail, Collection<String> to, String subject, String content) {
        return new EmailDto(fromEmail, fromEmail, to, List.of(), List.of(), subject, content, false, new FileBArray[]{});
    }

    public static EmailDto of(String fromEmail, String fromName, Collection<String> to, String subject, String content) {
        return new EmailDto(fromEmail, fromName, to, List.of(), List.of(), subject, content, false, new FileBArray[]{});
    }

    public record FileBArray(byte[] data, String filename) {
    }

    @Override
    public String toString() {
        return "EmailDto{" +
            "fromEmail='" + fromEmail + '\'' +
            ", fromName='" + fromName + '\'' +
            ", to=" + to +
            ", cc=" + cc +
            ", bcc=" + bcc +
            ", subject='" + subject + '\'' +
            ", isHtml=" + isHtml +
            '}';
    }
}
