package se.vgregion.vatskenutrition.controller;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImageControllerTest {

    @Test
    public void formatUrl() {
        assertEquals("/documents/1234/asdf", ImageController.formatUrl("1234", "asdf"));
    }
}