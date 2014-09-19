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

import com.jayway.jsonpath.internal.spi.mapper.DefaultMappingProvider;
import com.jayway.jsonpath.internal.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static com.jayway.jsonpath.internal.Utils.notNull;
import static java.util.Arrays.asList;

public class Configuration {

    private static Defaults DEFAULTS = new Defaults() {

        private final MappingProvider mappingProvider = new DefaultMappingProvider();

        public JsonProvider jsonProvider() {
            return new JsonSmartJsonProvider();
        }

        @Override
        public Set<Option> options() {
            return EnumSet.noneOf(Option.class);
        }

        @Override
        public MappingProvider mappingProvider() {
            return mappingProvider;
        }
    };

    public static synchronized void setDefaults(Defaults defaults){
        DEFAULTS = defaults;
    }

    private final JsonProvider jsonProvider;
    private final MappingProvider mappingProvider;
    private final Set<Option> options;

    private Configuration(JsonProvider jsonProvider, MappingProvider mappingProvider, EnumSet<Option> options) {
        notNull(jsonProvider, "jsonProvider can not be null");
        notNull(mappingProvider, "mappingProvider can not be null");
        notNull(options, "options can not be null");
        this.jsonProvider = jsonProvider;
        this.mappingProvider = mappingProvider;
        this.options = Collections.unmodifiableSet(options);
    }

    public Configuration jsonProvider(JsonProvider newJsonProvider) {
        return Configuration.builder().jsonProvider(newJsonProvider).conversionProvider(mappingProvider).options(options).build();
    }

    public JsonProvider jsonProvider() {
        return jsonProvider;
    }

    public MappingProvider conversionProvider() {
        return mappingProvider;
    }

    public Configuration conversionProvider(MappingProvider newMappingProvider) {
        return Configuration.builder().jsonProvider(jsonProvider).conversionProvider(newMappingProvider).options(options).build();
    }

    public Configuration addOptions(Option... options) {
        EnumSet<Option> opts = EnumSet.noneOf(Option.class);
        opts.addAll(this.options);
        opts.addAll(asList(options));
        return Configuration.builder().jsonProvider(jsonProvider).conversionProvider(mappingProvider).options(opts).build();
    }
    public Configuration options(Option... options) {
        return Configuration.builder().jsonProvider(jsonProvider).conversionProvider(mappingProvider).options(options).build();
    }

    public Set<Option> getOptions() {
        return options;
    }

    public boolean containsOption(Option option){
        return options.contains(option);
    }


    public static Configuration defaultConfiguration() {
        return Configuration.builder().jsonProvider(DEFAULTS.jsonProvider()).options(DEFAULTS.options()).build();
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public static class ConfigurationBuilder {

        private JsonProvider jsonProvider;
        private MappingProvider mappingProvider;
        private EnumSet<Option> options = EnumSet.noneOf(Option.class);

        public ConfigurationBuilder jsonProvider(JsonProvider provider) {
            this.jsonProvider = provider;
            return this;
        }

        public ConfigurationBuilder conversionProvider(MappingProvider provider) {
            this.mappingProvider = provider;
            return this;
        }

        public ConfigurationBuilder options(Option... flags) {
            if(flags.length > 0) {
                this.options.addAll(asList(flags));
            }
            return this;
        }

        public ConfigurationBuilder options(Set<Option> options) {
            this.options.addAll(options);
            return this;
        }

        public Configuration build() {
            if (jsonProvider == null) {
                jsonProvider = DEFAULTS.jsonProvider();
            }
            if(mappingProvider == null){
                mappingProvider = DEFAULTS.mappingProvider();
            }
            return new Configuration(jsonProvider, mappingProvider, options);
        }
    }

    public interface Defaults {

        JsonProvider jsonProvider();

        Set<Option> options();

        MappingProvider mappingProvider();

    }
}
