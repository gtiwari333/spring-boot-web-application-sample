package gt.contentchecker;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    boolean allowed;
    String entityId;
    Request.RequestType requestType;

    public static Response withResult(Request msg, boolean result) {
        var resp = new Response();
        resp.setAllowed(result);
        resp.setEntityId(msg.entityId);
        resp.setRequestType(msg.requestType);

        return resp;
    }
}
