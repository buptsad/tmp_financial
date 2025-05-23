package com.example.app.ui.pages.AI;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class getResTest {

    @Test
    @DisplayName("parseAIResponse should extract content from valid JSON")
    void testParseAIResponseValid() {
        String json = "{ \"choices\": [ { \"message\": { \"content\": \"This is the answer.\" } } ] }";
        getRes ai = new getRes();
        String result = ai.parseAIResponse(json);
        assertEquals("This is the answer.", result);
    }

    @Test
    @DisplayName("parseAIResponse should return error string for invalid JSON")
    void testParseAIResponseInvalid() {
        String json = "not a json";
        getRes ai = new getRes();
        String result = ai.parseAIResponse(json);
        assertTrue(result.startsWith("Error"));
    }

    @Test
    @DisplayName("parseAIResponse should return error string for missing fields")
    void testParseAIResponseMissingFields() {
        String json = "{ \"choices\": [] }";
        getRes ai = new getRes();
        String result = ai.parseAIResponse(json);
        assertTrue(result.startsWith("Error") || result.isEmpty());
    }

    @AfterAll
    static void cleanUp() {
        // No files or folders created by getRes, nothing to clean up
    }
}