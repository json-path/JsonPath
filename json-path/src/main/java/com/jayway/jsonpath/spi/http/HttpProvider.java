package com.jayway.jsonpath.spi.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface HttpProvider {

    InputStream get(URL url) throws IOException;

}
