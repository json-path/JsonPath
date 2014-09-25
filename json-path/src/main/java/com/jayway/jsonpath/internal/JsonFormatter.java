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
package com.jayway.jsonpath.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFormatter {

    private static final Logger logger = LoggerFactory.getLogger(JsonFormatter.class);

    private static final String INDENT = "   ";

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static void appendIndent(StringBuilder sb, int count) {
        for (; count > 0; --count) sb.append(INDENT);
    }

    private static boolean isEscaped(StringBuilder sb, int index) {
        boolean escaped = false;
        int idx = Math.min(index, sb.length());
        try {
            while (idx > 0 && sb.charAt(--idx) == '\\') {
                escaped = !escaped;
            }
        } catch (Exception e){
            logger.warn("Failed to check escaped ", e);
        }
        return escaped;
    }

    public static String prettyPrint(String input) {

        input = input.replaceAll("[\\r\\n]", "");

        StringBuilder output = new StringBuilder(input.length() * 2);
        boolean quoteOpened = false;
        int depth = 0;

        for (int i = 0; i < input.length(); ++i) {
            char ch = input.charAt(i);

            switch (ch) {
                case '{':
                case '[':
                    output.append(ch);
                    if (!quoteOpened) {
                        output.append(NEW_LINE);
                        appendIndent(output, ++depth);
                    }
                    break;
                case '}':
                case ']':
                    if (quoteOpened)
                        output.append(ch);
                    else {
                        output.append(NEW_LINE);
                        appendIndent(output, --depth);
                        output.append(ch);
                    }
                    break;
                case '"':
                case '\'':
                    output.append(ch);
                    if (quoteOpened) {
                        if (!isEscaped(output, i))
                            quoteOpened = false;
                    } else quoteOpened = true;
                    break;
                case ',':
                    output.append(ch);
                    if (!quoteOpened) {
                        output.append(NEW_LINE);
                        appendIndent(output, depth);
                    }
                    break;
                case ':':
                    if (quoteOpened) output.append(ch);
                    else output.append(" : ");
                    break;
                default:
                    if (quoteOpened || !(ch == ' '))
                        output.append(ch);
                    break;
            }
        }
        return output.toString();
    }
}