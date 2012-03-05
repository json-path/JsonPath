package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JsonSmartJsonProvider;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 3/2/12
 * Time: 9:45 PM
 */
public abstract class JsonProviderFactory {

    public static JsonProvider getInstance() {
        return new JsonSmartJsonProvider();
    }

}
