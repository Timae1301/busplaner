package de.hsw.busplaner.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

/**
 * PatchUtil Klasse, um das JSON-Patch Objekt auf ein anderes Objekt zu mappen
 */
public class PatchUtil {

    // privater Konstruktor
    private PatchUtil() {
    }

    /**
     * Verändert das übergebene Objekt anhand des übergebenen JSON-Patch Objekts
     * 
     * @param <T>
     * @param patch
     * @param t
     * @param theClass
     * @return mit JSON-Patch bearbeitetes Objekt
     * @throws JsonPatchException
     * @throws JsonProcessingException
     */
    public static <T> T applyPatch(JsonPatch patch, T t, Class<T> theClass)
            throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(t, JsonNode.class));
        return objectMapper.treeToValue(patched, theClass);
    }

}
