package gt.common.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Something Bad Happened")
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -420530763778423325L;

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(String msg, Exception e) {
        super(msg, e);
    }
}
