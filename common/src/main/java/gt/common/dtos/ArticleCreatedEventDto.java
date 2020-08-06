package gt.common.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString
public class ArticleCreatedEventDto implements Serializable {

    private static final long serialVersionUID = -1007949715L;

    private Long id;
    private String title;

    private String content;

    private String username;
}
