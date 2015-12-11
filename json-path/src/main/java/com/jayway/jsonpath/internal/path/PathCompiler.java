package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.CharacterIndex;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.filter.FilterCompiler;
import com.jayway.jsonpath.internal.function.Parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Character.isDigit;
import static java.util.Arrays.asList;

public class PathCompiler {

    private static final char DOC_CONTEXT = '$';
    private static final char EVAL_CONTEXT = '@';

    private static final char OPEN_SQUARE_BRACKET = '[';
    private static final char CLOSE_SQUARE_BRACKET = ']';
    private static final char OPEN_PARENTHESIS = '(';
    private static final char CLOSE_PARENTHESIS = ')';
    private static final char OPEN_BRACE = '{';
    private static final char CLOSE_BRACE = '}';

    private static final char WILDCARD = '*';
    private static final char PERIOD = '.';
    private static final char SPACE = ' ';
    private static final char TAB = '\t';
    private static final char CR = '\r';
    private static final char LF = '\r';
    private static final char BEGIN_FILTER = '?';
    private static final char COMMA = ',';
    private static final char SPLIT = ':';
    private static final char MINUS = '-';
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';

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

            if(!(path.charAt(0) == DOC_CONTEXT)  && !(path.charAt(0) == EVAL_CONTEXT)){
                path = "$." + path;
            }
            if(path.endsWith(".")){
                fail("Path must not end with a '.' or '..'");
            }
            LinkedList filterStack = new LinkedList<Predicate>(asList(filters));
            Path p = new PathCompiler(path.trim(), filterStack).compile();
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

    private void readWhitespace() {
        while (path.inBounds()) {
            char c = path.currentChar();
            if (c != SPACE && c != TAB && c != LF && c != CR) {
                break;
            }
            path.incrementPosition(1);
        }
    }

    //[$ | @]
    private RootPathToken readContextToken() {

        readWhitespace();

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
            fail("Illegal character at position " + path.position() + " expected '.' or '[");
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
                        fail("Could not parse token starting at position " + path.position() + ". Expected ?, ', 0-9, * ");
            case PERIOD:
                return readDotToken(appender) ||
                        fail("Could not parse token starting at position " + path.position());
            case WILDCARD:
                return readWildCardToken(appender) ||
                        fail("Could not parse token starting at position " + path.position());
            default:
                return readPropertyOrFunctionToken(appender) ||
                        fail("Could not parse token starting at position " + path.position());
        }
    }

    //
    // . and ..
    //
    private boolean readDotToken(PathTokenAppender appender) {
        if (path.currentCharIs(PERIOD) && path.nextCharIs(PERIOD)) {
            appender.appendPathToken(PathTokenFactory.crateScanToken());
            path.incrementPosition(2);
        } else if (!path.hasMoreCharacters()) {
            throw new InvalidPathException("Path must not end with a '.");
        } else {
            path.incrementPosition(1);
        }
        if(path.currentCharIs(PERIOD)){
            throw new InvalidPathException("Character '.' on position " + path.position() + " is not valid.");
        }
        return readNextToken(appender);
    }

    //
    // fooBar or fooBar()
    //
    private boolean readPropertyOrFunctionToken(PathTokenAppender appender) {
        if (path.currentCharIs(OPEN_SQUARE_BRACKET) || path.currentCharIs(WILDCARD) || path.currentCharIs(PERIOD) || path.currentCharIs(SPACE)) {
            return false;
        }
        int startPosition = path.position();
        int readPosition = startPosition;
        int endPosition = 0;

        boolean isFunction = false;

        while (path.inBounds(readPosition)) {
            char c = path.charAt(readPosition);
            if (c == SPACE) {
                throw new InvalidPathException("Use bracket notion ['my prop'] if your property contains blank characters. position: " + path.position());
            }
            else if (c == PERIOD || c == OPEN_SQUARE_BRACKET) {
                endPosition = readPosition;
                break;
            }
            else if (c == OPEN_PARENTHESIS) {
                isFunction = true;
                endPosition = readPosition++;
                break;
            }
            readPosition++;
        }
        if (endPosition == 0) {
            endPosition = path.length();
        }


        List<Parameter> functionParameters = null;
        if (isFunction) {
            if (path.inBounds(readPosition+1)) {
                // read the next token to determine if we have a simple no-args function call
                char c = path.charAt(readPosition + 1);
                if (c != CLOSE_PARENTHESIS) {
                    // parse the arguments of the function - arguments that are inner queries will be single quoted parameters
                    functionParameters = parseFunctionParameters(readPosition);
                } else {
                    path.setPosition(readPosition + 1);
                }
            }
            else {
                path.setPosition(readPosition);
            }
        }
        else {
            path.setPosition(endPosition);
        }

        String property = path.subSequence(startPosition, endPosition).toString();
        if(isFunction){
            appender.appendPathToken(PathTokenFactory.createFunctionPathToken(property, functionParameters));
        } else {
            appender.appendPathToken(PathTokenFactory.createSinglePropertyPathToken(property, SINGLE_QUOTE));
        }

        return path.currentIsTail() || readNextToken(appender);
    }

    private List<Parameter> parseFunctionParameters(int readPosition) {
        PathToken currentToken;
        List<Parameter> parameters = new ArrayList<Parameter>();
        StringBuffer parameter = new StringBuffer();
        Boolean insideParameter = false;
        int braceCount = 0, parenCount = 1;
        while (path.inBounds(readPosition)) {
            char c = path.charAt(readPosition);

            if (c == OPEN_BRACE) {
                braceCount++;
            }
            else if (c == CLOSE_BRACE) {
                braceCount--;
                if (0 == braceCount && parameter.length() > 0) {
                    // inner parse the parameter expression to pass along to the function
                    LinkedList<Predicate> predicates = new LinkedList<Predicate>();
                    PathCompiler compiler = new PathCompiler(parameter.toString(), predicates);
                    Path path = compiler.compile();
                    parameters.add(new Parameter(path));
                    parameter.delete(0, parameter.length());
                }
            }
            else if (c == COMMA && braceCount == 0) {
                parameter.delete(0, parameter.length());
            }
            else {
                if (c == CLOSE_PARENTHESIS) {
                    parenCount--;
                    if (parenCount == 0) {
                        if (parameter.length() > 0) {
                            // inner parse the parameter expression to pass along to the function
                            LinkedList<Predicate> predicates = new LinkedList<Predicate>();
                            PathCompiler compiler = new PathCompiler(parameter.toString(), predicates);
                            parameters.add(new Parameter(compiler.compile()));
                        }
                        break;
                    }
                    else {
                        parameter.append(c);
                    }
                }
                else if (c == OPEN_PARENTHESIS) {
                    parenCount++;
                    parameter.append(c);
                }
                else {
                    parameter.append(c);
                }
            }
            readPosition++;
        }
        path.setPosition(readPosition);
        return parameters;
    }

    //
    // [?], [?,?, ..]
    //
    private boolean readPlaceholderToken(PathTokenAppender appender) {

        if (!path.currentCharIs(OPEN_SQUARE_BRACKET)) {
            return false;
        }
        int questionmarkIndex = path.indexOfNextSignificantChar(BEGIN_FILTER);
        if (questionmarkIndex == -1) {
            return false;
        }
        char nextSignificantChar = path.nextSignificantChar(questionmarkIndex);
        if (nextSignificantChar != CLOSE_SQUARE_BRACKET && nextSignificantChar != COMMA) {
            return false;
        }

        int expressionBeginIndex = path.position() + 1;
        int expressionEndIndex = path.nextIndexOf(expressionBeginIndex, CLOSE_SQUARE_BRACKET);

        if (expressionEndIndex == -1) {
            return false;
        }

        String expression = path.subSequence(expressionBeginIndex, expressionEndIndex).toString();

        String[] tokens = expression.split(",");

        if (filterStack.size() < tokens.length) {
            throw new InvalidPathException("Not enough predicates supplied for filter [" + expression + "] at position " + path.position());
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
        if (!path.currentCharIs(OPEN_SQUARE_BRACKET) && !path.nextSignificantCharIs(BEGIN_FILTER)) {
            return false;
        }

        int openStatementBracketIndex = path.position();
        int questionMarkIndex = path.indexOfNextSignificantChar(BEGIN_FILTER);
        if (questionMarkIndex == -1) {
            return false;
        }
        int openBracketIndex = path.indexOfNextSignificantChar(questionMarkIndex, OPEN_PARENTHESIS);
        if (openBracketIndex == -1) {
            return false;
        }
        int closeBracketIndex = path.indexOfClosingBracket(openBracketIndex, true, true);
        if (closeBracketIndex == -1) {
            return false;
        }
        if (!path.nextSignificantCharIs(closeBracketIndex, CLOSE_SQUARE_BRACKET)) {
            return false;
        }
        int closeStatementBracketIndex = path.indexOfNextSignificantChar(closeBracketIndex, CLOSE_SQUARE_BRACKET);

        String criteria = path.subSequence(openStatementBracketIndex, closeStatementBracketIndex + 1).toString();


        Predicate predicate = FilterCompiler.compile(criteria);
        //Predicate predicate = Filter.parse(criteria);
        appender.appendPathToken(PathTokenFactory.createPredicatePathToken(predicate));

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
        if (!path.currentCharIs(WILDCARD) && path.isOutOfBounds(path.position() + 1)) {
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

        int expressionBeginIndex = path.position() + 1;
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
        if (!path.currentCharIs(OPEN_SQUARE_BRACKET)) {
            return false;
        }
        char potentialStringDelimiter = path.nextSignificantChar();
        if (potentialStringDelimiter != SINGLE_QUOTE && potentialStringDelimiter != DOUBLE_QUOTE) {
          return false;
        }

        List<String> properties = new ArrayList<String>();

        int startPosition = path.position() + 1;
        int readPosition = startPosition;
        int endPosition = 0;
        boolean inProperty = false;

        while (path.inBounds(readPosition)) {
            char c = path.charAt(readPosition);

            if (c == CLOSE_SQUARE_BRACKET && !inProperty) {
                break;
            } else if (c == potentialStringDelimiter) {
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

        appender.appendPathToken(PathTokenFactory.createPropertyPathToken(properties, potentialStringDelimiter));

        return path.currentIsTail() || readNextToken(appender);
    }

    public static boolean fail(String message) {
        throw new InvalidPathException(message);
    }
}
