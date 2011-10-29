package com.jayway.jsonpath;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * User: kalle stenflo
 * Date: 2/2/11
 * Time: 2:08 PM
 */
public class PathUtil {

    /**
     * Checks if a path points to a single item or if it potentially returns multiple items
     * <p/>
     * a path is considered <strong>not</strong> definite if it contains a scan fragment ".."
     * or an array position fragment that is not based on a single index
     * <p/>
     * <p/>
     * absolute path examples:
     * <p/>
     * $store.book
     * $store.book[1].value
     * <p/>
     * not absolute path examples
     * <p/>
     * $..book
     * $.store.book[1,2]
     * $.store.book[?(@.category = 'fiction')]
     *
     * @param jsonPath the path to check
     * @return true if path is definite (points to single item)
     */
    public static boolean isPathDefinite(String jsonPath) {
        return !jsonPath.replaceAll("\"[^\"\\\\\\n\r]*\"", "").matches(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");
    }

    /**
     * Splits a path into fragments
     * <p/>
     * the path <code>$.store.book[1].category</code> returns ["$", "store", "book", "[1]", "category"]
     *
     * @param jsonPath path to split
     * @return fragments
     */
    public static List<String> splitPath(String jsonPath) {

        if (!jsonPath.startsWith("$") && !jsonPath.startsWith("$[")) {
            jsonPath = "$." + jsonPath;
        }

        LinkedList<String> fragments = new LinkedList<String>();

        Queue<Character> pathQueue = new LinkedList<Character>();
        for (char b : jsonPath.toCharArray()) {
            pathQueue.add(b);
        }

        while (!pathQueue.isEmpty()) {
            skip(pathQueue, ' ');
            char current = pathQueue.peek();

            switch (current) {
                case '$':
                    fragments.add(Character.toString(current));
                    pathQueue.poll();
                    break;

                case '.':
                    pathQueue.poll();
                    if (pathQueue.peek().equals('.')) {
                        pathQueue.poll();
                        fragments.add("..");

                        assertNotInvalidPeek(pathQueue, '.');
                    }
                    break;

                case '[':
                    fragments.add(extract(pathQueue, true, ']'));
                    break;

                default:
                    fragments.add(extract(pathQueue, false, '[', '.'));

            }
        }
        return fragments;
    }


    private static String extract(Queue<Character> pathQueue, boolean includeSopChar, char... stopChars) {
        StringBuilder sb = new StringBuilder();
        while (!pathQueue.isEmpty() && (!isStopChar(pathQueue.peek().charValue(), stopChars))) {

            char c = pathQueue.poll();

            if (isStopChar(c, stopChars)) {
                if (includeSopChar) {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }

        if (includeSopChar) {
            assertValidPeek(pathQueue, false, stopChars);
            sb.append(pathQueue.poll());
        } else {
            assertValidPeek(pathQueue, true, stopChars);
        }
        return unWrapProperty(sb);
    }

    private static String unWrapProperty(StringBuilder sb) {

        String src = sb.toString();

        src = trim(src, "'");
        src = trim(src, ")");
        src = trim(src, "(");
        src = trimLeft(src, "?");
        src = trimLeft(src, "@");

        if (src.length() > 5 && src.subSequence(0, 2).equals("['")) {
            src = src.substring(2);
            src = src.substring(0, src.length() - 2);
        }

        return src.trim();
    }

    private static String trim(String src, String trim) {
        return trimLeft(trimRight(src, trim), trim);
    }

    private static String trimRight(String src, String trim) {
        String scanFor = trim + " ";
        if (src.contains(scanFor)) {
            while (src.contains(scanFor)) {
                src = src.replace(scanFor, trim);
            }
        }
        return src;
    }

    private static String trimLeft(String src, String trim) {
        String scanFor = " " + trim;
        if (src.contains(scanFor)) {
            while (src.contains(scanFor)) {
                src = src.replace(scanFor, trim);
            }
        }
        return src;
    }

    private static boolean isStopChar(char c, char... scanFor) {
        boolean found = false;
        for (char check : scanFor) {
            if (check == c) {
                found = true;
                break;
            }
        }
        return found;
    }

    private static void skip(Queue<Character> pathQueue, char target) {
        if (pathQueue.isEmpty()) {
            return;
        }
        while (pathQueue.peek().charValue() == target) {
            pathQueue.poll();
        }
    }

    private static void assertNotInvalidPeek(Queue<Character> pathQueue, char... invalidChars) {
        if (pathQueue.isEmpty()) {
            return;
        }
        char peek = pathQueue.peek();
        for (char check : invalidChars) {
            if (check == peek) {
                throw new InvalidPathException("Char: " + peek + " at current position is not valid!");
            }
        }
    }

    private static void assertValidPeek(Queue<Character> pathQueue, boolean acceptEmpty, char... validChars) {
        if (pathQueue.isEmpty() && acceptEmpty) {
            return;
        }
        if (pathQueue.isEmpty()) {
            throw new InvalidPathException("Path is incomplete");
        }
        boolean found = false;
        char peek = pathQueue.peek();
        for (char check : validChars) {
            if (check == peek) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new InvalidPathException("Path is invalid");
        }
    }

}
