package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.token.ArrayIndexOperation;
import com.jayway.jsonpath.internal.token.ArraySliceOperation;
import com.jayway.jsonpath.internal.token.PathTokenAppender;
import com.jayway.jsonpath.internal.token.PathTokenFactory;
import com.jayway.jsonpath.internal.token.RootPathToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Character.isDigit;
import static java.lang.Math.min;
import static java.util.Arrays.asList;

public class PathCompiler {

    private static final Logger logger = LoggerFactory.getLogger(PathCompiler.class);

    private static final char DOC_CONTEXT = '$';
    private static final char EVAL_CONTEXT = '@';
    private static final char OPEN_SQUARE_BRACKET = '[';
    private static final char CLOSE_SQUARE_BRACKET = ']';
    private static final char OPEN_BRACKET = '(';
    private static final char CLOSE_BRACKET = ')';
    private static final char WILDCARD = '*';
    private static final char PERIOD = '.';
    private static final char SPACE = ' ';
    private static final char QUESTIONMARK = '?';
    private static final char COMMA = ',';
    private static final char SPLIT = ':';
    private static final char MINUS = '-';
    private static final char ESCAPE = '\\';
    private static final char TICK = '\'';

    private static final Cache cache = new Cache(200);

    private final LinkedList<Predicate> filterStack;
    private final CharacterIndex path;

    private PathCompiler(String path, LinkedList<Predicate> filterStack) {
        this.filterStack = filterStack;
        this.path = new CharacterIndex(path);
    }

    private Path compile() {
        RootPathToken root = readContextToken();
        return new CompiledPath(root, root.getPathFragment().equals("$"));
    }

    public static Path compile(String path, final Predicate... filters) {
        try {
            path = path.trim();

            if(!path.startsWith("$") && !path.startsWith("@")){
                path = "$." + path;
            }
            if(path.endsWith("..")){
                fail("Path must not end wid a scan operation '..'");
            }
            LinkedList filterStack = new LinkedList<Predicate>(asList(filters));
//            String cacheKey = Utils.concat(path, filterStack.toString());
//            Path p = cache.get(cacheKey);
//            if (p == null) {
            Path p = new PathCompiler(path.trim(), filterStack).compile();
//                cache.put(cacheKey, p);
//            }
            return p;
        } catch (Exception e) {
            InvalidPathException ipe;
            if (e instanceof InvalidPathException) {
                ipe = (InvalidPathException) e;
            } else {
                ipe = new InvalidPathException(e);
            }
            throw ipe;
        }
    }

    //[$ | @]
    private RootPathToken readContextToken() {

        if (!path.currentCharIs(DOC_CONTEXT) && !path.currentCharIs(EVAL_CONTEXT)) {
            throw new InvalidPathException("Path must start with '$' or '@'");
        }

        RootPathToken pathToken = PathTokenFactory.createRootPathToken(path.currentChar());
        PathTokenAppender appender = pathToken.getPathTokenAppender();

        if (path.currentIsTail()) {
            return pathToken;
        }

        path.incrementPosition(1);

        if(path.currentChar() != PERIOD && path.currentChar() != OPEN_SQUARE_BRACKET){
            fail("Illegal character at position " + path.position + " expected '.' or '[");
        }

        readNextToken(appender);

        return pathToken;
    }

    //
    //
    //
    private boolean readNextToken(PathTokenAppender appender) {

        char c = path.currentChar();

        switch (c) {
            case OPEN_SQUARE_BRACKET:
                return readBracketPropertyToken(appender) ||
                        readArrayToken(appender) ||
                        readWildCardToken(appender) ||
                        readFilterToken(appender) ||
                        readPlaceholderToken(appender) ||
                        fail("Could not parse bracket statement at position " + path.position);
            case PERIOD:
                return readDotSeparatorToken(appender) ||
                        readScanToken(appender) ||
                        fail("Could not parse token at position " + path.position);
            case WILDCARD:
                return readWildCardToken(appender) ||
                        fail("Could not parse token at position " + path.position);
            default:
                return readPropertyToken(appender) ||
                        fail("Could not parse token at position " + path.position);
        }
    }

    //
    // .
    //
    private boolean readDotSeparatorToken(PathTokenAppender appender) {
        if (!path.currentCharIs('.') || path.nextCharIs('.')) {
            return false;
        }
        if (!path.hasMoreCharacters()) {
            throw new InvalidPathException("Path must not end with a '.");
        }
//        if (path.nextSignificantCharIs('[')) {
//            throw new InvalidPathException("A bracket may not follow a '.");
//        }

        path.incrementPosition(1);

        return readNextToken(appender);
    }

    //
    // fooBar
    //
    private boolean readPropertyToken(PathTokenAppender appender) {
        if (path.currentCharIs(OPEN_SQUARE_BRACKET) || path.currentCharIs(WILDCARD) || path.currentCharIs(PERIOD) || path.currentCharIs(SPACE)) {
            return false;
        }
        int startPosition = path.position;
        int readPosition = startPosition;
        int endPosition = 0;

        while (path.inBounds(readPosition)) {
            char c = path.charAt(readPosition);
            if (c == SPACE) {
                throw new InvalidPathException("Use bracket notion ['my prop'] if your property contains blank characters. position: " + path.position);
            }
            if (c == PERIOD || c == OPEN_SQUARE_BRACKET) {
                endPosition = readPosition;
                break;
            }
            readPosition++;
        }
        if (endPosition == 0) {
            endPosition = path.length();
        }

        path.setPosition(endPosition);

        String property = path.subSequence(startPosition, endPosition).toString();

        appender.appendPathToken(PathTokenFactory.createSinglePropertyPathToken(property));

        return path.currentIsTail() || readNextToken(appender);
    }

    //
    // [?], [?,?, ..]
    //
    private boolean readPlaceholderToken(PathTokenAppender appender) {

        if (!path.currentCharIs(OPEN_SQUARE_BRACKET)) {
            return false;
        }
        int questionmarkIndex = path.indexOfNextSignificantChar(QUESTIONMARK);
        if (questionmarkIndex == -1) {
            return false;
        }
        char nextSignificantChar = path.nextSignificantChar(questionmarkIndex);
        if (nextSignificantChar != CLOSE_SQUARE_BRACKET && nextSignificantChar != COMMA) {
            return false;
        }

        int expressionBeginIndex = path.position + 1;
        int expressionEndIndex = path.nextIndexOf(expressionBeginIndex, CLOSE_SQUARE_BRACKET);

        if (expressionEndIndex == -1) {
            return false;
        }

        String expression = path.subSequence(expressionBeginIndex, expressionEndIndex).toString();

        String[] tokens = expression.split(",");

        if (filterStack.size() < tokens.length) {
            throw new InvalidPathException("Not enough predicates supplied for filter [" + expression + "] at position " + path.position);
        }

        Collection<Predicate> predicates = new ArrayList<Predicate>();
        for (String token : tokens) {
            token = token != null ? token.trim() : token;
            if (!"?".equals(token == null ? "" : token)) {
                throw new InvalidPathException("Expected '?' but found " + token);
            }
            predicates.add(filterStack.pop());
        }

        appender.appendPathToken(PathTokenFactory.createPredicatePathToken(predicates));

        path.setPosition(expressionEndIndex + 1);

        return path.currentIsTail() || readNextToken(appender);
    }

    //
    // [?(...)]
    //
    private boolean readFilterToken(PathTokenAppender appender) {
        if (!path.currentCharIs(OPEN_SQUARE_BRACKET) && !path.nextSignificantCharIs(QUESTIONMARK)) {
            return false;
        }

        int openStatementBracketIndex = path.position;
        int questionMarkIndex = path.indexOfNextSignificantChar(QUESTIONMARK);
        if (questionMarkIndex == -1) {
            return false;
        }
        int openBracketIndex = path.indexOfNextSignificantChar(questionMarkIndex, OPEN_BRACKET);
        if (openBracketIndex == -1) {
            return false;
        }
        int closeBracketIndex = path.indexOfClosingBracket(openBracketIndex + 1, true);
        if (closeBracketIndex == -1) {
            return false;
        }
        if (!path.nextSignificantCharIs(closeBracketIndex, CLOSE_SQUARE_BRACKET)) {
            return false;
        }
        int closeStatementBracketIndex = path.indexOfNextSignificantChar(closeBracketIndex, CLOSE_SQUARE_BRACKET);

        String criteria = path.subSequence(openStatementBracketIndex, closeStatementBracketIndex + 1).toString();

        appender.appendPathToken(PathTokenFactory.createPredicatePathToken(Filter.parse(criteria)));

        path.setPosition(closeStatementBracketIndex + 1);

        return path.currentIsTail() || readNextToken(appender);

    }

    //
    // [*]
    // *
    //
    private boolean readWildCardToken(PathTokenAppender appender) {

        boolean inBracket = path.currentCharIs(OPEN_SQUARE_BRACKET);

        if (inBracket && !path.nextSignificantCharIs(WILDCARD)) {
            return false;
        }
        if (!path.currentCharIs(WILDCARD) && path.isOutOfBounds(path.position + 1)) {
            return false;
        }
        if (inBracket) {
            int wildCardIndex = path.indexOfNextSignificantChar(WILDCARD);
            if (!path.nextSignificantCharIs(wildCardIndex, CLOSE_SQUARE_BRACKET)) {
                throw new InvalidPathException("Expected wildcard token to end with ']' on position " + wildCardIndex + 1);
            }
            int bracketCloseIndex = path.indexOfNextSignificantChar(wildCardIndex, CLOSE_SQUARE_BRACKET);
            path.setPosition(bracketCloseIndex + 1);
        } else {
            path.incrementPosition(1);
        }

        appender.appendPathToken(PathTokenFactory.createWildCardPathToken());

        return path.currentIsTail() || readNextToken(appender);
    }

    //
    // [1], [1,2, n], [1:], [1:2], [:2]
    //
    private boolean readArrayToken(PathTokenAppender appender) {

        if (!path.currentCharIs(OPEN_SQUARE_BRACKET)) {
            return false;
        }
        char nextSignificantChar = path.nextSignificantChar();
        if (!isDigit(nextSignificantChar) && nextSignificantChar != MINUS && nextSignificantChar != SPLIT) {
            return false;
        }

        int expressionBeginIndex = path.position + 1;
        int expressionEndIndex = path.nextIndexOf(expressionBeginIndex, CLOSE_SQUARE_BRACKET);

        if (expressionEndIndex == -1) {
            return false;
        }

        String expression = path.subSequence(expressionBeginIndex, expressionEndIndex).toString().replace(" ", "");

        if ("*".equals(expression)) {
            return false;
        }

        //check valid chars
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (!isDigit(c) && c != COMMA && c != MINUS && c != SPLIT) {
                return false;
            }
        }

        boolean isSliceOperation = expression.contains(":");

        if (isSliceOperation) {
            ArraySliceOperation arraySliceOperation = ArraySliceOperation.parse(expression);
            appender.appendPathToken(PathTokenFactory.createSliceArrayPathToken(arraySliceOperation));
        } else {
            ArrayIndexOperation arrayIndexOperation = ArrayIndexOperation.parse(expression);
            appender.appendPathToken(PathTokenFactory.createIndexArrayPathToken(arrayIndexOperation));
        }

        path.setPosition(expressionEndIndex + 1);

        return path.currentIsTail() || readNextToken(appender);
    }

    //
    // ['foo']
    //
    private boolean readBracketPropertyToken(PathTokenAppender appender) {
        if (!path.currentCharIs(OPEN_SQUARE_BRACKET) || !path.nextSignificantCharIs(TICK)) {
            return false;
        }

        List<String> properties = new ArrayList<String>();

        int startPosition = path.position + 1;
        int readPosition = startPosition;
        int endPosition = 0;
        boolean inProperty = false;

        while (path.inBounds(readPosition)) {
            char c = path.charAt(readPosition);

            if (c == CLOSE_SQUARE_BRACKET) {
                if (inProperty) {
                    throw new InvalidPathException("Expected property to be closed at position " + readPosition);
                }
                break;
            } else if (c == TICK) {
                if (inProperty) {
                    endPosition = readPosition;
                    properties.add(path.subSequence(startPosition, endPosition).toString());
                    inProperty = false;
                } else {
                    startPosition = readPosition + 1;
                    inProperty = true;
                }
            }
            readPosition++;
        }

        int endBracketIndex = path.indexOfNextSignificantChar(endPosition, CLOSE_SQUARE_BRACKET) + 1;

        path.setPosition(endBracketIndex);

        appender.appendPathToken(PathTokenFactory.createPropertyPathToken(properties));

        return path.currentIsTail() || readNextToken(appender);
    }

    //
    // ..
    //
    private boolean readScanToken(PathTokenAppender appender) {
        if (!path.currentCharIs(PERIOD) || !path.nextCharIs(PERIOD)) {
            return false;
        }
        appender.appendPathToken(PathTokenFactory.crateScanToken());
        path.incrementPosition(2);

        return readNextToken(appender);
    }


    public static boolean fail(String message) {
        throw new InvalidPathException(message);
    }


    private static class CharacterIndex {

        private final CharSequence charSequence;
        private int position;

        private CharacterIndex(CharSequence charSequence) {
            this.charSequence = charSequence;
            this.position = 0;
        }

        private int length() {
            return charSequence.length();
        }

        private char charAt(int idx) {
            return charSequence.charAt(idx);
        }

        private char currentChar() {
            return charSequence.charAt(position);
        }

        private boolean currentCharIs(char c) {
            return (charSequence.charAt(position) == c);
        }

        private boolean nextCharIs(char c) {
            return inBounds(position + 1) && (charSequence.charAt(position + 1) == c);
        }

        private int incrementPosition(int charCount) {
            return setPosition(position + charCount);
        }

        private int setPosition(int newPosition) {
            position = min(newPosition, charSequence.length() - 1);
            return position;
        }

        private int indexOfClosingBracket(int startPosition, boolean skipStrings) {
            int opened  = 1;
            int readPosition = startPosition;
            while (inBounds(readPosition)) {
                if (skipStrings) {
                    if (charAt(readPosition) == TICK) {
                        while (inBounds(readPosition)) {
                            readPosition++;
                            if (charAt(readPosition) == TICK && charAt(readPosition - 1) != ESCAPE) {
                                readPosition++;
                                break;
                            }
                        }
                    }
                }
                if (charAt(readPosition) == OPEN_BRACKET) {
                    opened++;
                }
                if (charAt(readPosition) == CLOSE_BRACKET) {
                    opened--;
                    if(opened == 0){
                        return readPosition;
                    }
                }
                readPosition++;
            }
            return -1;
        }

        public int indexOfNextSignificantChar(char c) {
            return indexOfNextSignificantChar(position, c);
        }

        public int indexOfNextSignificantChar(int startPosition, char c) {
            int readPosition = startPosition + 1;
            while (!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
                readPosition++;
            }
            if (charAt(readPosition) == c) {
                return readPosition;
            } else {
                return -1;
            }
        }

        public int nextIndexOf(int startPosition, char c) {
            int readPosition = startPosition;
            while (!isOutOfBounds(readPosition)) {
                if (charAt(readPosition) == c) {
                    return readPosition;
                }
                readPosition++;
            }
            return -1;
        }

        public boolean nextSignificantCharIs(int startPosition, char c) {
            int readPosition = startPosition + 1;
            while (!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
                readPosition++;
            }
            return !isOutOfBounds(readPosition) && charAt(readPosition) == c;
        }

        public boolean nextSignificantCharIs(char c) {
            return nextSignificantCharIs(position, c);
        }

        public char nextSignificantChar() {
            return nextSignificantChar(position);
        }

        public char nextSignificantChar(int startPosition) {
            int readPosition = startPosition + 1;
            while (!isOutOfBounds(readPosition) && charAt(readPosition) == SPACE) {
                readPosition++;
            }
            if (!isOutOfBounds(readPosition)) {
                return charAt(readPosition);
            } else {
                return ' ';
            }
        }

        private boolean currentIsTail() {
            return isOutOfBounds(position + 1);
        }

        private boolean hasMoreCharacters() {
            return inBounds(position + 1);
        }

        private boolean inBounds(int idx) {
            return (idx >= 0) && (idx < charSequence.length());
        }

        private boolean isOutOfBounds(int idx) {
            return !inBounds(idx);
        }

        private CharSequence subSequence(int start, int end) {
            return charSequence.subSequence(start, end);
        }
    }
}



