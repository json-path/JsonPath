/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.spi;

import com.jayway.jsonpath.spi.impl.JsonSmartJsonProvider;

/**
 * @author Kalle Stenflo
 */

public abstract class JsonProviderFactory {

    private static ThreadLocal<JsonProviderFactory> providerFactory = new ThreadLocal<JsonProviderFactory>();

    /**
     * Get a {@link JsonProvider}, created using a default implementation. The
     * factory's behavior can be overridden using
     * {@link #setProviderFactory(JsonProviderFactory)}.
     * 
     * @return a JsonProvider
     */
    public static JsonProvider getProvider() {
        JsonProviderFactory factory = providerFactory.get();
        if (factory != null){
            return factory.createProvider();
        }
        return new JsonSmartJsonProvider();
    }

    public abstract JsonProvider createProvider(); 
    
    /**
     * Sets a {@link JsonProviderFactory} to be used for the current thread.
     * This uses a {@link ThreadLocal} internally which should be cleaned up by
     * a call to {@link #reset()} when done.
     * 
     * @param factory the {@link JsonProviderFactory} to be used for the
     * current thread.
     */
    public static void setProviderFactory(JsonProviderFactory factory) {
        providerFactory.set(factory);
    }
    
    /**
     * Resets the {@link ThreadLocal} {@link JsonProviderFactory} and restores
     * the default {@link JsonProviderFactory} implementation.
     */
    public static void reset() {
      providerFactory.remove();
    }


}
