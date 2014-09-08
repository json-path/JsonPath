package com.jayway.jsonpath.internal;

import java.util.ArrayList;
import java.util.List;

public class PathFormalizer extends Parser {

    private static final Fragment ROOT = new Fragment(Type.ROOT, "$");
    private static final Fragment SCAN = new Fragment(Type.SCAN, "..");


    StringBuilder formalized = new StringBuilder();

    public PathFormalizer(String path) {
        super(path);
    }


    //$.store.book[?(@.isbn)].isbn
    //$['store']['book'][?(@['isbn'])]['isbn']

    public String formalize() {
        TokenBuffer buffer = new TokenBuffer();
        do {
            char current = next();

            switch (current) {
                case '$':
                    buffer.append("$").flush();
                    break;
                case '.':
                    if (!buffer.isEmpty()) {
                        buffer.flush();
                    }
                    if (peekIs(Token.DOT)) {
                        next();
                        buffer.append("..").flush();
                    }
                    break;
                case '[':
                    if (!buffer.isEmpty()) {
                        buffer.flush();
                    }
                    break;
                case ']':
                    if (!buffer.isEmpty()) {
                        buffer.flush();
                    }
                    break;
                case '?':
                    if (peekIs(Token.OPEN_PARENTHESIS)) {
                        buffer.append("?" + nextUntil(Token.CLOSE_BRACKET));
                        buffer.flush();
                    }
                    break;
                default:
                    buffer.append(current);
                    break;
            }
        } while (hasNext());

        if (!buffer.isEmpty()) {
            buffer.flush();
        }
        for (Fragment f : buffer.getFragments()) {
            formalized.append(f.toString());
        }
        return formalized.toString();

    }

    private static class TokenBuffer {

        private List<Fragment> fragments = new ArrayList<Fragment>();
        private StringBuilder sb = new StringBuilder();

        public TokenBuffer append(String s) {
            sb.append(s);
            return this;
        }

        public TokenBuffer append(char c) {
            sb.append(c);
            return this;
        }

        public void flush() {
            fragments.add(new Fragment(Type.PROPERTY, sb.toString().trim()));
            sb = new StringBuilder();
        }

        public boolean isEmpty() {
            return sb.length() == 0;
        }

        public List<Fragment> getFragments() {
            return fragments;
        }
    }

    private Fragment createFragment(String data) {
        if ("$".equals(data)) {
            return ROOT;
        } else if ("..".equals(data)) {
            return SCAN;
        } else if (isInts(data, true)) {
            return new Fragment(Type.INDEX, new String(data));
        } else {
            return new Fragment(Type.PROPERTY, new String(data));
        }

    }

    private static class Fragment {


        private Type type;
        private String frag;

        private Fragment(Type type, String frag) {
            this.type = type;
            this.frag = frag;
        }
        /*
                //"[-1:]"  sliceFrom
        //"[:1]"   sliceTo
        //"[0:5]"  sliceBetween
        //"[1]"
        //"[1,2,3]"
        //"[(@.length - 1)]"
         */

        private static Fragment create(String str) {

            boolean isProperty = str.startsWith("'") && str.endsWith("'");

            if (isProperty) {
                return new Fragment(Type.PROPERTY, new String(str.substring(1, str.length()-1)));
            } else if ("$".equals(str)) {
                return ROOT;
            } else if ("..".equals(str)) {
                return SCAN;
            } else if ("*".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else if ("-1:".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else if (":1".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else if ("1:2".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else if ("1".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else if ("1,2,3".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else if ("(@.length() - 1)".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else if ("?(@.foo == 'bar')".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else if ("1,2,3".equals(str)) {
                return new Fragment(Type.INDEX, new String(str));
            } else {
                return new Fragment(Type.PROPERTY, new String(str));
            }
        }

        @Override
        public String toString() {
            return frag;
        }
    }

    private static enum Type {
        ROOT,
        SCAN,
        PROPERTY,
        INDEX
    }



}
