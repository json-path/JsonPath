package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.compiler.ArrayPathToken;
import com.jayway.jsonpath.internal.compiler.PredicatePathToken;
import com.jayway.jsonpath.internal.compiler.PathToken;
import com.jayway.jsonpath.internal.compiler.PropertyPathToken;
import com.jayway.jsonpath.internal.compiler.RootPathToken;
import com.jayway.jsonpath.internal.compiler.ScanPathToken;
import com.jayway.jsonpath.internal.compiler.WildcardPathToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.internal.Utils.notEmpty;
import static java.util.Arrays.asList;

public class PathCompiler {

    private static final Logger logger = LoggerFactory.getLogger(PathCompiler.class);

    private static final String PROPERTY_OPEN = "['";
    private static final String PROPERTY_CLOSE = "']";
    private static final char DOCUMENT = '$';
    private static final char ANY = '*';
    private static final char PERIOD = '.';
    private static final char BRACKET_OPEN = '[';
    private static final char BRACKET_CLOSE = ']';
    private static final char SPACE = ' ';
    private static final Cache cache = new Cache(200);



    public static Path compile(String path, Predicate... filters) {
        notEmpty(path, "Path may not be null empty");
        path = path.trim();

        LinkedList<Predicate> filterList = new LinkedList<Predicate>(asList(filters));

        if (!path.startsWith("$")) {
            path = "$." + path;
        }

        String cacheKey = path + filterList.toString();
        Path p = cache.get(cacheKey);
        if(p != null){
            return p;
        }

        RootPathToken root = null;


        char[] chars = path.toCharArray();
        int i = 0;
        int positions;
        String fragment = "";

        do {
            char current = chars[i];

            switch (current) {
                case SPACE:
                    throw new InvalidPathException("Space not allowed in path");
                case DOCUMENT:
                    fragment = "$";
                    i++;
                    break;
                case BRACKET_OPEN:
                    positions = fastForwardUntilClosed(chars, i);
                    fragment = new String(chars, i, positions);
                    i += positions;
                    break;
                case PERIOD:
                    i++;
                    if (chars[i] == PERIOD) {
                        //This is a deep scan
                        fragment = "..";
                        i++;
                    } else {
                        positions = fastForward(chars, i);
                        if (positions == 0) {
                            continue;

                        } else if (positions == 1 && chars[i] == '*') {
                            fragment = new String("[*]");
                        } else {
                            assertValidFieldChars(chars, i, positions);

                            fragment = PROPERTY_OPEN + new String(chars, i, positions) + PROPERTY_CLOSE;
                        }
                        i += positions;
                    }
                    break;
                case ANY:
                    fragment = new String("[*]");
                    i++;
                    break;
                default:
                    positions = fastForward(chars, i);

                    fragment = PROPERTY_OPEN + new String(chars, i, positions) + PROPERTY_CLOSE;
                    i += positions;
                    break;
            }
            if (root == null) {
                root = (RootPathToken) PathComponentAnalyzer.analyze(fragment, filterList);
            } else {
                root.append(PathComponentAnalyzer.analyze(fragment, filterList));
            }

        } while (i < chars.length);

        Path pa = new CompiledPath(root);

        cache.put(cacheKey, pa);

        return pa;
    }

    private static void assertValidFieldChars(char[] chars, int start, int positions) {
        int i = start;
        while (i < start + positions) {
            char c = chars[i];

            if (!Character.isLetterOrDigit(c) && c != '-' && c != '_' && c != '$' && c != '@') {
                throw new InvalidPathException("Invalid field name! Use bracket notation if your filed names does not match pattern: ([a-zA-Z@][a-zA-Z0-9@\\$_\\-]*)$");
            }
            i++;
        }
    }

    private static int fastForward(char[] chars, int index) {
        int skipCount = 0;
        while (index < chars.length) {
            char current = chars[index];
            if (current == PERIOD || current == BRACKET_OPEN || current == SPACE) {
                break;
            }
            index++;
            skipCount++;
        }
        return skipCount;
    }

    private static int fastForwardUntilClosed(char[] chars, int index) {
        int skipCount = 0;
        int nestedBrackets = 0;

        //First char is always '[' no need to check it
        index++;
        skipCount++;

        while (index < chars.length) {
            char current = chars[index];

            index++;
            skipCount++;

            if (current == BRACKET_CLOSE && nestedBrackets == 0) {
                break;
            }
            if (current == BRACKET_OPEN) {
                nestedBrackets++;
            }
            if (current == BRACKET_CLOSE) {
                nestedBrackets--;
            }
        }
        return skipCount;
    }


    //---------------------------------------------
    //
    //
    //
    //---------------------------------------------
    static class PathComponentAnalyzer {

        private static final Pattern FILTER_PATTERN = Pattern.compile("^\\[\\s*\\?\\s*[,\\s*\\?]*?\\s*]$"); //[?] or [?, ?, ...]
        private char[] chars;
        private int i;
        private char current;

        private final LinkedList<Predicate> filterList;
        private final String pathFragment;

        PathComponentAnalyzer(String pathFragment, LinkedList<Predicate> filterList) {
            this.pathFragment = pathFragment;
            this.filterList = filterList;
        }

        static PathToken analyze(String pathFragment, LinkedList<Predicate> filterList) {
            return new PathComponentAnalyzer(pathFragment, filterList).analyze();
        }

        public PathToken analyze() {

            if ("$".equals(pathFragment)) return new RootPathToken();
            else if ("..".equals(pathFragment)) return new ScanPathToken();
            else if ("[*]".equals(pathFragment)) return new WildcardPathToken();
            else if (".*".equals(pathFragment)) return new WildcardPathToken();
            else if ("[?]".equals(pathFragment)) return new PredicatePathToken(filterList.poll());

            else if (FILTER_PATTERN.matcher(pathFragment).matches()) {
                final int criteriaCount = Utils.countMatches(pathFragment, "?");
                List<Predicate> filters = new ArrayList<Predicate>(criteriaCount);
                for (int i = 0; i < criteriaCount; i++) {
                    filters.add(filterList.poll());
                }
                return new PredicatePathToken(filters);
            }

            this.chars = pathFragment.toCharArray();
            this.i = 0;
            do {
                current = chars[i];

                switch (current) {
                    case '?':
                        return analyzeCriteriaSequence();
                    case '\'':
                        return analyzeProperty();
                    default:
                        if (Character.isDigit(current) || current == ':' || current == '-' || current == '@') {
                            return analyzeArraySequence();
                        }
                        i++;
                        break;
                }


            } while (i < chars.length);

            throw new InvalidPathException("Could not analyze path component: " + pathFragment);
        }

        //[?(@.foo)]
        //[?(@['foo'])]
        //[?(@.foo == 'bar')]
        //[?(@['foo']['bar'] == 'bar')]
        //[?(@ == 'bar')]
        private PathToken analyzeCriteriaSequence() {
            StringBuilder pathBuffer = new StringBuilder();
            StringBuilder operatorBuffer = new StringBuilder();
            StringBuilder valueBuffer = new StringBuilder();
            List<Predicate> criteria = new ArrayList<Predicate>();

            int bracketCount = 0;

            boolean functionBracketOpened = false;
            boolean functionBracketClosed = false;
            boolean propertyOpen = false;

            current = chars[++i]; //skip the '?'

            while (current != ']' || bracketCount != 0) {

                switch (current) {
                    case '[':
                        bracketCount++;
                        pathBuffer.append(current);
                        break;

                    case ']':
                        bracketCount--;
                        pathBuffer.append(current);
                        break;

                    case '@':
                        pathBuffer.append('$');
                        break;

                    case '(':
                        if(!propertyOpen) {
                            functionBracketOpened = true;
                            break;
                        }

                    case ')':
                        if(!propertyOpen) {
                            functionBracketClosed = true;
                            break;
                        }

                    default:
                        if('\'' == current){
                            if (propertyOpen){
                                propertyOpen = false;
                            } else {
                                propertyOpen = true;
                            }
                        }
                        if (bracketCount == 0 && isOperatorChar(current)) {
                            operatorBuffer.append(current);

                        } else if (bracketCount == 0 && isLogicOperatorChar(current)) {

                            if (isLogicOperatorChar(chars[i + 1])) {
                                ++i;
                            }
                            criteria.add(createCriteria(pathBuffer, operatorBuffer, valueBuffer));

                            pathBuffer = new StringBuilder();
                            operatorBuffer = new StringBuilder();
                            valueBuffer = new StringBuilder();

                        } else if (operatorBuffer.length() > 0) {
                            valueBuffer.append(current);
                        } else {
                            pathBuffer.append(current);
                        }

                        break;
                }
                current = chars[++i];
            }

            if (!functionBracketOpened || !functionBracketClosed) {
                throw new InvalidPathException("Function wrapping brackets are not matching. A filter function must match [?(<statement>)]");
            }

            criteria.add(createCriteria(pathBuffer, operatorBuffer, valueBuffer));

            Filter filter2 =  Filter.filter(criteria);

            return new PredicatePathToken(filter2);
        }

        private static Criteria createCriteria(StringBuilder pathBuffer, StringBuilder operatorBuffer, StringBuilder valueBuffer) {
            return Criteria.create(pathBuffer.toString().trim(), operatorBuffer.toString().trim(), valueBuffer.toString().trim());
        }

        private static boolean isAnd(char c) {
            return c == '&';
        }

        private static boolean isOr(char c) {
            if (c == '|') {
                throw new UnsupportedOperationException("OR operator is not supported.");
            }
            return false;
        }

        private static boolean isLogicOperatorChar(char c) {
            return isAnd(c) || isOr(c);
        }

        private static boolean isOperatorChar(char c) {
            return c == '=' || c == '!' || c == '<' || c == '>';
        }

        //"['foo']"
        private PathToken analyzeProperty() {
            List<String> properties = new ArrayList<String>();
            StringBuilder buffer = new StringBuilder();

            boolean propertyIsOpen = false;

            while (current != ']') {
                switch (current) {
                    case '\'':
                        if (propertyIsOpen) {
                            properties.add(buffer.toString());
                            buffer = new StringBuilder();
                            propertyIsOpen = false;
                        } else {
                            propertyIsOpen = true;
                        }
                        break;
                    default:
                        if (propertyIsOpen) {
                            buffer.append(current);
                        }
                        break;
                }
                current = chars[++i];
            }
            return new PropertyPathToken(properties);
        }


        //"[-1:]"  sliceFrom
        //"[:1]"   sliceTo
        //"[0:5]"  sliceBetween
        //"[1]"
        //"[1,2,3]"
        //"[(@.length - 1)]"
        private PathToken analyzeArraySequence() {
            StringBuilder buffer = new StringBuilder();
            List<Integer> numbers = new ArrayList<Integer>();

            boolean contextSize = (current == '@');
            boolean sliceTo = false;
            boolean sliceFrom = false;
            boolean sliceBetween = false;
            boolean indexSequence = false;
            boolean singleIndex = false;

            if (contextSize) {

                current = chars[++i];
                current = chars[++i];
                while (current != '-') {
                    if (current == ' ' || current == '(' || current == ')') {
                        current = chars[++i];
                        continue;
                    }
                    buffer.append(current);
                    current = chars[++i];
                }
                String function = buffer.toString();
                buffer = new StringBuilder();
                if (!function.equals("size") && !function.equals("length")) {
                    throw new InvalidPathException("Invalid function: @." + function + ". Supported functions are: [(@.length - n)] and [(@.size() - n)]");
                }
                while (current != ')') {
                    if (current == ' ') {
                        current = chars[++i];
                        continue;
                    }
                    buffer.append(current);
                    current = chars[++i];
                }

            } else {


                while (Character.isDigit(current) || current == ',' || current == ' ' || current == ':' || current == '-') {

                    switch (current) {
                        case ' ':
                            break;
                        case ':':
                            if (buffer.length() == 0) {
                                //this is a tail slice [:12]
                                sliceTo = true;
                                current = chars[++i];
                                while (Character.isDigit(current) || current == ' ' || current == '-') {
                                    if (current != ' ') {
                                        buffer.append(current);
                                    }
                                    current = chars[++i];
                                }
                                numbers.add(Integer.parseInt(buffer.toString()));
                                buffer = new StringBuilder();
                            } else {
                                //we now this starts with [12:???
                                numbers.add(Integer.parseInt(buffer.toString()));
                                buffer = new StringBuilder();
                                current = chars[++i];

                                //this is a tail slice [:12]
                                while (Character.isDigit(current) || current == ' ' || current == '-') {
                                    if (current != ' ') {
                                        buffer.append(current);
                                    }
                                    current = chars[++i];
                                }

                                if (buffer.length() == 0) {
                                    sliceFrom = true;
                                } else {
                                    sliceBetween = true;
                                    numbers.add(Integer.parseInt(buffer.toString()));
                                    buffer = new StringBuilder();
                                }
                            }
                            break;
                        case ',':
                            numbers.add(Integer.parseInt(buffer.toString()));
                            buffer = new StringBuilder();
                            indexSequence = true;
                            break;
                        default:
                            buffer.append(current);
                            break;
                    }
                    if (current == ']') {
                        break;
                    }
                    current = chars[++i];
                }
            }
            if (buffer.length() > 0) {
                numbers.add(Integer.parseInt(buffer.toString()));
            }
            singleIndex = (numbers.size() == 1) && !sliceTo && !sliceFrom && !contextSize;

            if (logger.isTraceEnabled()) {
                logger.debug("numbers are                : {}", numbers.toString());
                logger.debug("sequence is singleNumber   : {}", singleIndex);
                logger.debug("sequence is numberSequence : {}", indexSequence);
                logger.debug("sequence is sliceFrom      : {}", sliceFrom);
                logger.debug("sequence is sliceTo        : {}", sliceTo);
                logger.debug("sequence is sliceBetween   : {}", sliceBetween);
                logger.debug("sequence is contextFetch   : {}", contextSize);
                logger.debug("---------------------------------------------");
            }
            ArrayPathToken.Operation operation = null;

            if (singleIndex) operation = ArrayPathToken.Operation.SINGLE_INDEX;
            else if (indexSequence) operation = ArrayPathToken.Operation.INDEX_SEQUENCE;
            else if (sliceFrom) operation = ArrayPathToken.Operation.SLICE_FROM;
            else if (sliceTo) operation = ArrayPathToken.Operation.SLICE_TO;
            else if (sliceBetween) operation = ArrayPathToken.Operation.SLICE_BETWEEN;
            else if (contextSize) operation = ArrayPathToken.Operation.CONTEXT_SIZE;

            assert operation != null;

            return new ArrayPathToken(numbers, operation);

        }
    }
}