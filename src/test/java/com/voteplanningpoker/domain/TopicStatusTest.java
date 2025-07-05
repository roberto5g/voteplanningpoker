package com.voteplanningpoker.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TopicStatusTest {
    @Test
    void testGetStatus() {
        assertEquals("OPEN", TopicStatus.OPEN.getStatus());
        assertEquals("CLOSED", TopicStatus.CLOSED.getStatus());
    }

    @Test
    void testFromStringValid() {
        assertEquals(TopicStatus.OPEN, TopicStatus.fromString("OPEN"));
        assertEquals(TopicStatus.CLOSED, TopicStatus.fromString("CLOSED"));
        assertEquals(TopicStatus.OPEN, TopicStatus.fromString("open"));
        assertEquals(TopicStatus.CLOSED, TopicStatus.fromString("closed"));
    }

    @Test
    void testFromStringInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TopicStatus.fromString("INVALID");
        });
        assertTrue(exception.getMessage().contains("Unknown status"));
    }
}

