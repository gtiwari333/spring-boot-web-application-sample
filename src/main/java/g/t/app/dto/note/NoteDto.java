package g.t.app.dto.note;

import lombok.Data;

@Data
public class NoteDto {

    private Long id;

    private String title;

    private String content;

    private Long userId;


}
