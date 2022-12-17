package gt.app.modules.article;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ArticleCreateDto {

    @NotNull
    MultipartFile[] files = {};
    private String title;
    private String content;
}
