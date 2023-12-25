package gt.report;

import gt.common.config.CommonExceptionHandler;
import io.micrometer.tracing.Tracer;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ExceptionHandler extends CommonExceptionHandler {
    public ExceptionHandler(Tracer tracer) {
        super(tracer);
    }
}
