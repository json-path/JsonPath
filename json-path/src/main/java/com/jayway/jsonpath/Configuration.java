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
package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static com.jayway.jsonpath.internal.Utils.notNull;
import static java.util.Arrays.asList;

public class Configuration {

    private final JsonProvider provider;
    private final Set<Option> options;

    private Configuration(JsonProvider provider, EnumSet<Option> options) {
        notNull(provider, "provider can not be null");
        notNull(options, "options can not be null");
        this.provider = provider;
        this.options = Collections.unmodifiableSet(options);
    }

    public Configuration provider(JsonProvider provider) {
        return Configuration.builder().jsonProvider(provider).options(options).build();
    }

    public JsonProvider getProvider() {
        return provider;
    }

    public Configuration addOptions(Option... options) {
        EnumSet<Option> opts = EnumSet.noneOf(Option.class);
        opts.addAll(this.options);
        opts.addAll(asList(options));
        return Configuration.builder().jsonProvider(provider).options(opts).build();
    }
    public Configuration options(Option... options) {
        return Configuration.builder().jsonProvider(provider).options(options).build();
    }

    public Set<Option> getOptions() {
        return options;
    }

    public boolean containsOption(Option option){
        return options.contains(option);
    }


    public static Configuration defaultConfiguration() {
        return new Configuration(JsonProviderFactory.createProvider(), EnumSet.noneOf(Option.class));
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public static class ConfigurationBuilder {

        private JsonProvider provider;
        private EnumSet<Option> options = EnumSet.noneOf(Option.class);

        public ConfigurationBuilder jsonProvider(JsonProvider provider) {
            this.provider = provider;
            return this;
        }

        public ConfigurationBuilder options(Option... flags) {
            this.options.addAll(asList(flags));
            return this;
        }

        public ConfigurationBuilder options(Set<Option> options) {
            this.options.addAll(options);
            return this;
        }

        public Configuration build() {
            if (provider == null) {
                provider = JsonProviderFactory.createProvider();
            }
            return new Configuration(provider, options);
        }
    }
}
