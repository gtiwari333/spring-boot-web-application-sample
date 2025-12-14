package gt.common.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class ArticleSummaryDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1007949715L;

    private Long id;
    private String title;

    private String content;

    private String username;
}
