package de.hsw.busplaner.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

public class PatchUtil {
    public static <T> T applyPatch(JsonPatch patch, T t, Class<T> theClass)
            throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(t, JsonNode.class));
        return objectMapper.treeToValue(patched, theClass);
    }

}
