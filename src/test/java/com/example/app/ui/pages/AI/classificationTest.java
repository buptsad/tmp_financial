package com.example.app.ui.pages.AI;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class classificationTest {

    @Test
    @DisplayName("parseAIResponse should extract content from valid JSON")
    void testParseAIResponseValid() {
        String json = "{ \"choices\": [ { \"message\": { \"content\": \"Food,Other\" } } ] }";
        classification ai = new classification();
        String result = ai.parseAIResponse(json);
        assertEquals("Food,Other", result);
    }

    @Test
    @DisplayName("parseAIResponse should return error string for invalid JSON")
    void testParseAIResponseInvalid() {
        String json = "not a json";
        classification ai = new classification();
        String result = ai.parseAIResponse(json);
        assertTrue(result.startsWith("Error"));
    }

    @Test
    @DisplayName("parseAIResponse should return error string for missing fields")
    void testParseAIResponseMissingFields() {
        String json = "{ \"choices\": [] }";
        classification ai = new classification();
        String result = ai.parseAIResponse(json);
        assertTrue(result.startsWith("Error") || result.isEmpty());
    }

    @AfterAll
    static void cleanUp() {
        // No files or folders created by classification, nothing to clean up
    }
}