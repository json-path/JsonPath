package com.jayway.jsonpath.matchers.helpers;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.ClassLoader.getSystemResourceAsStream;

public class ResourceHelpers {
    public static String resource(String resource) {
        try {
            return IOUtils.toString(getSystemResourceAsStream(resource));
        } catch (IOException e) {
            throw new AssertionError("Resource not found", e);
        }
    }

    public static File resourceAsFile(String resource) {
        try {
            URL systemResource = getSystemResource(resource);
            URI uri = systemResource.toURI();
            Path path = Paths.get(uri);
            return path.toFile();
        } catch (URISyntaxException e) {
            throw new AssertionError("URI syntax error", e);
        }
    }
}
