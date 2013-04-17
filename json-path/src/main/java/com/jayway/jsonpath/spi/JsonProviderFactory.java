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

	private static Class defaultProvider = null;
	
    public static JsonProviderFactory factory = new JsonProviderFactory() {
        @Override
        protected JsonProvider create() {
			JsonProvider provider = null;
			try {
				if(defaultProvider != null) {
					provider = (JsonProvider)defaultProvider.newInstance();
				}
			} catch(Throwable t) {
			
			}
			if(provider == null) {
				provider = new JsonSmartJsonProvider();
			}
			return provider;
            //return new JacksonProvider();
        }
    };

    public static JsonProvider createProvider() {
        return factory.create();
    }

    public static synchronized void setDefaultProvider(Class<? extends JsonProvider> jsonProvider) {
        defaultProvider = jsonProvider;
    }

    protected abstract JsonProvider create();

}
