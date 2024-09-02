package br.com.victorbarberino.kaori.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Método para converter uma string JSON em um objeto JsonNode
    public static JsonNode parse(String jsonString) throws IOException {
        return objectMapper.readTree(jsonString);
    }
}
