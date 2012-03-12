package com.jayway.jsonpath.spi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/10/12
 * Time: 7:38 AM
 */
public interface HttpProvider {

    InputStream get(URL url) throws IOException;

}
