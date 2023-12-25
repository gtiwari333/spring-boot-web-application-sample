package gt.app.exception;

import gt.common.config.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends BaseException {

    public RecordNotFoundException(String description) {
        super(description);
    }

    public RecordNotFoundException(String requestedObjectName, String requestedByField, Object requestedByParam) {
        super(String.format("%s not found with %s = '%s'", requestedObjectName, requestedByField, requestedByParam));
    }
}
