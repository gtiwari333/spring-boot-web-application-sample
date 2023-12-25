package gt.app.exception;

import gt.common.config.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class OperationNotAllowedException extends BaseException {

    public OperationNotAllowedException(String what) {
        super(what);
    }
}
