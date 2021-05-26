package gt.profanity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Request implements Serializable {
    String text;
    String postBackTopic;
    String entityId;
    RequestType requestType;

    public static Request withArticle(String text, String postBackTopic, String entityId, RequestType requestType) {
        var r = new Request();
        r.text = text;
        r.postBackTopic = postBackTopic;
        r.requestType = requestType;
        r.entityId = entityId;
        return r;
    }

    public enum RequestType {
        COMMENT, ARTICLE
    }
}
