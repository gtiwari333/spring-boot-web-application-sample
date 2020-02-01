package gt.app.modules.topic;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopicDTO {
    private Long id;
    private String name;


    public TopicDTO(String name) {
        this.name = name;
    }
}
