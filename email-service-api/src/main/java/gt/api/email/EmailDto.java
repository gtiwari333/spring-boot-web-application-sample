package gt.api.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;

@Data
public class EmailDto {

    String from;
    Collection<String> to = new HashSet<>();
    Collection<String> cc = new HashSet<>();
    Collection<String> bcc = new HashSet<>();
    String subject;
    String content;
    boolean isHtml;
    FileBArray[] files;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileBArray {
        byte[] data;
        String filename;
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
