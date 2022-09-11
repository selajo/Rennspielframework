package com.mapeditor.controller;

import org.junit.Test;
import static org.junit.Assert.*;

public class MapExceptionTest {
    @Test
    public void test_MapException_message() {
        String message = "Testfehler";
        MapException mapException = new MapException(message);
        assertEquals(message, mapException.getMessage());
    }
}
