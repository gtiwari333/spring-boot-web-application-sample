package gt.api.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;

@Data
@Builder
public class EmailDto {

    String fromName;
    String fromEmail;
    Collection<String> to = new HashSet<>();
    Collection<String> cc = new HashSet<>();
    Collection<String> bcc = new HashSet<>();
    String subject;
    String content;
    boolean isHtml;
    FileBArray[] files;

    public FileBArray[] getFiles() {
        if (files == null) {
            return new FileBArray[]{};
        }
        return files;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FileBArray {
        byte[] data;
        String filename;
    }

    @Override
    public String toString() {
        return "EmailDto{" +
            "fromName='" + fromName + '\'' +
            "fromEmail='" + fromEmail + '\'' +
            ", to=" + to +
            ", cc=" + cc +
            ", bcc=" + bcc +
            ", subject='" + subject + '\'' +
            '}';
    }
}
