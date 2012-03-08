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

import com.jayway.jsonpath.spi.impl.JacksonProvider;

/**
 * @author Kalle Stenflo
 */
public abstract class MappingProviderFactory {


    public static MappingProviderFactory factory = new MappingProviderFactory() {

        private MappingProvider provider = null;

        @Override
        protected MappingProvider create() {
            if (this.provider == null) {
                synchronized (MappingProviderFactory.class) {
                    try {
                        Class.forName("org.codehaus.jackson.map.ObjectMapper");

                        provider = new JacksonProvider();
                        return provider;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("org.codehaus.jackson.map.ObjectMapper not found on classpath. This is an optional dependency needed for POJO conversions.", e);
                    }
                }
            } else {
                return provider;
            }
        }
    };

    protected abstract MappingProvider create();


    public static MappingProvider createProvider() {
        return factory.create();
    }
}
