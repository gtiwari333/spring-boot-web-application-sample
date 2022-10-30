package gt.app.modules.article;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

@Data
public class ArticleCreateDto {

    @NotNull
    MultipartFile[] files = {};
    private String title;
    private String content;
}
