package g.t.app.dto.note;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class NoteEditDto {

    @NotNull
    MultipartFile[] files;
    private String title;
    private String content;

}
