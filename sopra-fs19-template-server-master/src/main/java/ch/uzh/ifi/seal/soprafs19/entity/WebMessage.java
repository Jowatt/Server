package ch.uzh.ifi.seal.soprafs19.entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonEncoding;
import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.jackson.schema.JsonSchema;

import static org.springframework.data.repository.init.ResourceReader.Type.JSON;

public class WebMessage {

    private String message;

    public WebMessage (String content) {
        this.message = content;
    }

    public String getContent() {
        return message;
    }
}
