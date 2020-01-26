package gt.app.modules.note;

import gt.app.domain.NoteStatus;
import lombok.Data;

@Data
public class NoteReviewDto {

    private Long id;
    private NoteStatus verdict;

}
