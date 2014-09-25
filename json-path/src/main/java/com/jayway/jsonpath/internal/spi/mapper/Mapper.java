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
package com.jayway.jsonpath.internal.spi.mapper;

import com.jayway.jsonpath.Configuration;

import java.util.Set;

import static com.jayway.jsonpath.internal.Utils.notNull;

public interface Mapper {

    Set<ConvertiblePair> getConvertibleTypes();

    Object convert(Object src, Class<?> srcType, Class<?> targetType, Configuration conf);


    /**
     * Holder for a source-to-target class pair.
     */
    public static final class ConvertiblePair {

        private final Class<?> sourceType;

        private final Class<?> targetType;

        /**
         * Create a new source-to-target pair.
         *
         * @param sourceType the source type
         * @param targetType the target type
         */
        public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
            notNull(sourceType, "Source type must not be null");
            notNull(targetType, "Target type must not be null");
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public Class<?> getSourceType() {
            return this.sourceType;
        }

        public Class<?> getTargetType() {
            return this.targetType;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ConvertiblePair)) return false;

            ConvertiblePair that = (ConvertiblePair) o;

            if (!sourceType.equals(that.sourceType)) return false;
            if (!targetType.equals(that.targetType)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = sourceType.hashCode();
            result = 31 * result + targetType.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return this.sourceType.getName() + " -> " + this.targetType.getName();
        }
    }
}