package gt.api.email;

import java.util.Collection;
import java.util.List;

public record EmailDto(String from, Collection<String> to, Collection<String> cc, Collection<String> bcc,
                       String subject, String content, boolean isHtml, FileBArray[] files) {

    public static EmailDto of(String from, Collection<String> to, String subject, String content) {
        return new EmailDto(from, to, List.of(), List.of(), subject, content, false, new FileBArray[]{});
    }

    public record FileBArray(byte[] data, String filename) {
    }

    @Override
    public String toString() {
        return "EmailDto{" +
            "from='" + from + '\'' +
            ", to=" + to +
            ", cc=" + cc +
            ", bcc=" + bcc +
            ", subject='" + subject + '\'' +
            '}';
    }
}
