package testando;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Parser {
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    // String -> JsonNode
    public static JsonNode parser(String jsonString) throws JsonMappingException, JsonProcessingException {
    	
        return objectMapper.readTree(jsonString);
        
    }

    // JsonNode -> Objeto
    public static <T> T fromJSON(JsonNode node, Class<T> obj) throws JsonProcessingException, IllegalArgumentException{

        return objectMapper.treeToValue(node, obj);

    }

    // Objeto -> JsonNode
    public static JsonNode toJson(Object obj) {

        return objectMapper.valueToTree(obj);

    }

    // JsonNode -> String
    public static String stringfy(JsonNode node) throws JsonProcessingException {

        return objectMapper.writer()
        .with(SerializationFeature.INDENT_OUTPUT)
        .writeValueAsString(node);

    }
    
}
