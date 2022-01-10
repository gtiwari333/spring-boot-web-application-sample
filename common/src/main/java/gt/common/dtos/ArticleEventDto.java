package gt.common.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ArticleEventDto implements Serializable {

    private static final long serialVersionUID = -1007949715L;

    private Long id;
    private String title;

    private String content;

    private String username;
    private String[] tags;

    private LocalDateTime date = LocalDateTime.now();
}
