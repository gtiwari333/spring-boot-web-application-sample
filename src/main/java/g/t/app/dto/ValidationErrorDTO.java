package g.t.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorDTO {

    private List<FieldErrorDTO> fieldErrors = new ArrayList<>();

    public void addFieldError(String field, String message) {
        FieldErrorDTO error = new FieldErrorDTO(field, message);
        fieldErrors.add(error);
    }

}

@Data
@AllArgsConstructor
class FieldErrorDTO {
    private String field;
    private String message;
}
