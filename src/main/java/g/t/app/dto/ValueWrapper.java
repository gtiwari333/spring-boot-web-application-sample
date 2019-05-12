package g.t.app.dto;

import lombok.Data;

@Data
public class ValueWrapper<T> {
    private T value;
}
