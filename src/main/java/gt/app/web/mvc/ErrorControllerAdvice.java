package gt.app.web.mvc;

import gt.app.modules.file.RetrievalException;
import gt.app.modules.file.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ErrorControllerAdvice {


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(final Throwable throwable, final Model model) {
        log.error("Exception during execution of application", throwable);

        String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String storageException(final StorageException throwable, final Model model) {
        log.error("Exception during execution of application", throwable);
        model.addAttribute("errorMessage", "Failed to store file");
        return "error";
    }


    @ExceptionHandler(RetrievalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String retrievalException(final RetrievalException throwable, final Model model) {
        log.error("Exception during execution of application", throwable);
        model.addAttribute("errorMessage", "Failed to read file");
        return "error";
    }

}
