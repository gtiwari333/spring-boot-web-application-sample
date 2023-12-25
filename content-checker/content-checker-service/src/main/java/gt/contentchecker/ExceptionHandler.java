package gt.contentchecker;

import gt.common.config.CommonExceptionHandler;
import io.micrometer.tracing.Tracer;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ExceptionHandler extends CommonExceptionHandler {
    ExceptionHandler(Tracer tracer) {
        super(tracer);
    }
}
