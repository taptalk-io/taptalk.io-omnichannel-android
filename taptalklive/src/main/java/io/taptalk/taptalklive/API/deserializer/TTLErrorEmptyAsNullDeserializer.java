package io.taptalk.taptalklive.API.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel;


public class TTLErrorEmptyAsNullDeserializer extends JsonDeserializer<TTLErrorModel> {

    private static final String LOG_TAG = TTLErrorEmptyAsNullDeserializer.class.getSimpleName();

    @Override
    public TTLErrorModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();
        if (node.size() == 0) {
            return null;
        }
        TTLErrorModel error = new TTLErrorModel();
        error.setCode(node.get("code").asText());
        error.setMessage(node.get("message").asText());
        return error;
    }
}
