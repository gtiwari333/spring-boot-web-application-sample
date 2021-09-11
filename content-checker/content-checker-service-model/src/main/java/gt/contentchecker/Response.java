package gt.contentchecker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response implements Serializable {
    ContentCheckOutcome contentCheckOutcome;
    String entityId;
    Request.RequestType requestType;

    public static Response withResult(Request msg, ContentCheckOutcome result) {
        var resp = new Response();
        resp.setContentCheckOutcome(result);
        resp.setEntityId(msg.entityId);
        resp.setRequestType(msg.requestType);

        return resp;
    }
}
