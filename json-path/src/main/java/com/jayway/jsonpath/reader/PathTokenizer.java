package com.jayway.jsonpath.reader;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.spi.JsonProvider;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/4/11
 * Time: 9:53 PM
 */
public class PathTokenizer implements Iterable<PathToken> {

    private String path;
    private char[] pathChars;
    private int index = 0;
    private List<PathToken> pathTokens = new LinkedList<PathToken>();

    public PathTokenizer(String jsonPath, JsonProvider jsonProvider) {

        if (!jsonPath.startsWith("$") && !jsonPath.startsWith("$[")) {
            jsonPath = "$." + jsonPath;
        }
        this.path = jsonPath;
        this.pathChars = path.toCharArray();

        for (String pathFragment : splitPath()) {
            pathTokens.add(new PathToken(pathFragment));
        }
    }

    public List<String> getFragments(){
        List<String> fragments = new LinkedList<String>();
        for (PathToken pathToken : pathTokens) {
            fragments.add(pathToken.getFragment());
        }
        return fragments;
    }

    public String getPath() {
        return path;
    }

    public Iterator<PathToken> iterator() {
        return pathTokens.iterator();
    }


    //--------------------------------------------
    //
    // Split path
    //
    //--------------------------------------------
    private boolean isEmpty() {
        return index == pathChars.length;
    }

    private char peek() {
        return pathChars[index];
    }

    private char poll() {
        char peek = peek();
        index++;
        return peek;
    }

    public List<String> splitPath() {

        List<String> fragments = new LinkedList<String>();
        while (!isEmpty()) {
            skip(' ');
            char current = peek();

            switch (current) {
                case '$':
                    fragments.add(Character.toString(current));
                    poll();
                    break;

                case '.':
                    poll();
                    if (peek() == '.') {
                        poll();
                        fragments.add("..");

                        assertNotInvalidPeek('.');
                    }
                    break;

                case '[':
                    fragments.add(extract(true, ']'));
                    break;

                default:
                    fragments.add(extract(false, '[', '.'));

            }
        }
        return fragments;
    }


    private String extract(boolean includeSopChar, char... stopChars) {
        StringBuilder sb = new StringBuilder();
        while (!isEmpty() && (!isStopChar(peek(), stopChars))) {

            char c = poll();

            if (isStopChar(c, stopChars)) {
                if (includeSopChar) {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        if (includeSopChar) {
            assertValidPeek(false, stopChars);
            sb.append(poll());
        } else {
            assertValidPeek(true, stopChars);
        }
        return clean(sb);
    }

    private String clean(StringBuilder sb) {

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

    private String trim(String src, String trim) {
        return trimLeft(trimRight(src, trim), trim);
    }

    private String trimRight(String src, String trim) {
        String scanFor = trim + " ";
        if (src.contains(scanFor)) {
            while (src.contains(scanFor)) {
                src = src.replace(scanFor, trim);
            }
        }
        return src;
    }

    private String trimLeft(String src, String trim) {
        String scanFor = " " + trim;
        if (src.contains(scanFor)) {
            while (src.contains(scanFor)) {
                src = src.replace(scanFor, trim);
            }
        }
        return src;
    }

    private boolean isStopChar(char c, char... scanFor) {
        boolean found = false;
        for (char check : scanFor) {
            if (check == c) {
                found = true;
                break;
            }
        }
        return found;
    }

    private void skip(char target) {
        if (isEmpty()) {
            return;
        }
        while (pathChars[index] == target) {
            poll();
        }
    }

    private void assertNotInvalidPeek(char... invalidChars) {
        if (isEmpty()) {
            return;
        }
        char peek = peek();
        for (char check : invalidChars) {
            if (check == peek) {
                throw new InvalidPathException("Char: " + peek + " at current position is not valid!");
            }
        }
    }

    private void assertValidPeek(boolean acceptEmpty, char... validChars) {
        if (isEmpty() && acceptEmpty) {
            return;
        }
        if (isEmpty()) {
            throw new InvalidPathException("Path is incomplete");
        }
        boolean found = false;
        char peek = peek();
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
