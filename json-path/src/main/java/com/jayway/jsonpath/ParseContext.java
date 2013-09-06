package com.jayway.jsonpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * User: kalle
 * Date: 8/30/13
 * Time: 12:03 PM
 */
public interface ParseContext {

    ReadContext parse(String json);
    ReadContext parse(Object json);
    ReadContext parse(InputStream  json);
    ReadContext parse(File json) throws IOException;
    ReadContext parse(URL json) throws IOException;
}
