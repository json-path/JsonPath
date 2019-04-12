package com.jayway.jsonpath.internal.path;

import static java.lang.Character.isDigit;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.CharacterIndex;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.internal.filter.FilterCompiler;
import com.jayway.jsonpath.internal.function.FilterFunctionFactory;
import com.jayway.jsonpath.internal.function.Parameter;

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
    private static final char LF = '\n';
    private static final char BEGIN_FILTER = '?';
    private static final char COMMA = ',';
    private static final char SPLIT = ':';
    private static final char MINUS = '-';
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';
    private static final char ESCAPE = '\\';

    private final LinkedList<Predicate> filterStack;
    private final CharacterIndex path;

    private PathCompiler(String path, LinkedList<Predicate> filterStack){
        this(new CharacterIndex(path), filterStack);
    }

    private PathCompiler(CharacterIndex path, LinkedList<Predicate> filterStack){
        this.filterStack = filterStack;
        this.path = path;
    }

    private Path compile() {
        RootPathToken root = readContextToken();
        return new CompiledPath(root, root.getPathFragment().equals("$"));
    }

    public static Path compile(String path, final Predicate... filters) {
        try {
            CharacterIndex ci = new CharacterIndex(path);
            ci.trim();

            if(!( ci.charAt(0) == DOC_CONTEXT)  && !( ci.charAt(0) == EVAL_CONTEXT)){
                ci = new CharacterIndex("$." + path);
                ci.trim();
            }
            if(ci.lastCharIs('.')){
                fail("Path must not end with a '.' or '..'");
            }
            LinkedList<Predicate> filterStack = new LinkedList<Predicate>(asList(filters));
            Path p = new PathCompiler(ci, filterStack).compile();
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
            if (!isWhitespace(c)) {
                break;
            }
            path.incrementPosition(1);
        }
    }

    private Boolean isPathContext(char c) {
        return (c == DOC_CONTEXT || c == EVAL_CONTEXT);
    }

    //[$ | @]
    private RootPathToken readContextToken() {

        readWhitespace();

        if (!isPathContext(path.currentChar())) {
            throw new InvalidPathException("Path must start with '$' or '@'");
        }

        RootPathToken pathToken = PathTokenFactory.createRootPathToken(path.currentChar());

        if (path.currentIsTail()) {
            return pathToken;
        }

        path.incrementPosition(1);

        if(path.currentChar() != PERIOD && path.currentChar() != OPEN_SQUARE_BRACKET){
            fail("Illegal character at position " + path.position() + " expected '.' or '['");
        }

        PathTokenAppender appender = pathToken.getPathTokenAppender();
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
                endPosition = readPosition;
                break;
            }
            readPosition++;
        }
        if (endPosition == 0) {
            endPosition = path.length();
        }
        path.setPosition(endPosition);
        String name = path.subSequence(startPosition, endPosition).toString();
        
        if (isFunction) {
            Class functionClass = FilterFunctionFactory.FUNCTIONS.get(name);
            List<String> list = extractParameters(name);
            if(list.isEmpty()) {
            	appender.appendPathToken(functionClass!=null
            		?PathTokenFactory.createFilterPathToken(name,null)
            		:PathTokenFactory.createFunctionPathToken(name, Collections.<Parameter>emptyList()));
            } else {
            	if(functionClass!=null) {
            		Filter filter = FilterCompiler.compile(list.get(0));
            		appender.appendPathToken(PathTokenFactory.createFilterPathToken(name, filter));
            	} else {
            		List<Parameter> params = new ArrayList<Parameter>();
	            	for(String s:list) {
	            		Parameter param = null;
	            		if(s.startsWith("$") || s.endsWith("@")) {
	                        PathCompiler compiler = new PathCompiler(s,  new LinkedList<Predicate>());
	                        param = new Parameter(compiler.compile());
	            		}else {
	            			param = new Parameter(s);
	            		}
	            		params.add(param);
	            	}
            		appender.appendPathToken(PathTokenFactory.createFunctionPathToken(name, params));
            	}
            }
        } else {
            appender.appendPathToken(PathTokenFactory.createSinglePropertyPathToken(name, SINGLE_QUOTE));
        }
        return path.currentIsTail() || readNextToken(appender);
    }

    /**
     * Parse the filter applying on a filter function call,
     * 
     * @return
     *      An Filter that is processed via the function.  Typically functions either process
     *      an array of values and/or can consume parameters in addition to the values provided from the consumption of
     *      an array.
     */
    private List<String> extractParameters(String name) {
        Integer groupParen = 0, groupBracket = 0, groupBrace = 0, groupDQuote = 0, groupQuote = 0;
        
        boolean next = false;
        boolean keep = true;
        boolean escape = false;
        boolean endofstream = path.currentChar()!= OPEN_PARENTHESIS?true:false;

        StringBuilder filterBuilder = new StringBuilder();
        List<String> parameters = new ArrayList<String>();

        while (path.inBounds() && !endofstream) {
            char c = path.currentChar();
            path.incrementPosition(1);
            next = false;
            keep = true;
            switch (c) {
            	case ESCAPE:
            		if(escape) {
            			escape = false;
            			break;
            		}
            		escape = true;
            	break;
            	case SPACE: 
	            	if (0 == groupDQuote && 0 == groupQuote) {
	            		keep = false;
	            	}
	            	escape = false;
            	break;
                case DOUBLE_QUOTE:
                	if(escape){
                		escape = false;
                		break;
                	}
                    if (groupDQuote > 0) {
                        groupDQuote--;
                    }
                    else {
                        groupDQuote++;
                    }
                    break;  
                case SINGLE_QUOTE:
                	if(escape) {
                		escape = false;
                		break;
                	}
                    if (groupQuote > 0) {
                        groupQuote--;
                    } else {
                        groupQuote++;
                    }
                    break;
                case OPEN_PARENTHESIS:
                	if(escape) {
                		escape = false;
                		break;
                	}
                    groupParen++;
                    break;
                case OPEN_BRACE:
                	if(escape) {
                		escape = false;
                		break;
                	}
                    groupBrace++;
                    break;
                case OPEN_SQUARE_BRACKET:
                	if(escape) {
                		escape = false;
                		break;
                	}
                    groupBracket++;
                    break;
                case CLOSE_BRACE:
                	if(escape) {
                		escape = false;
                		break;
                	}
                    groupBrace--;
                    if (0 > groupBrace) {
                        throw new InvalidPathException("Unexpected close brace '}' at character position: " + path.position());
                    }
                    break;
                case CLOSE_SQUARE_BRACKET:
                	if(escape) {
                		escape = false;
                		break;
                	}
                    groupBracket--;
                    if (0 > groupBracket) {
                        throw new InvalidPathException("Unexpected close bracket ']' at character position: " + path.position());
                    }
                    break;
                case CLOSE_PARENTHESIS:
                	if(escape) {
                		escape = false;
                		break;
                	}
                    groupParen--;
                    if (0 == groupQuote && 0 == groupDQuote && 0 == groupBrace && 0 == groupBracket && 0 == groupParen) {                    	
                    	endofstream = true;
                    	next = true;
                    }
                    break;
                 case COMMA:
                	if(escape) {
                		escape = false;
                		break;
                	}
                	if(0 == groupDQuote && 0 == groupQuote &&  0 == groupBrace && 0 == groupBracket && 1 == groupParen) {
	                	next = true;
                	}
                	break;
            }
            if(next) {
            	if(filterBuilder.length() > 0) {
            		parameters.add(filterBuilder.toString());
            	}
            	filterBuilder = new StringBuilder();
            	
            } else if(keep) {
            	if((c == OPEN_PARENTHESIS && groupParen==1)|| (c == CLOSE_PARENTHESIS && groupParen==0)) {
            		continue;
            	}
            	filterBuilder.append(c);
            }
        }
        if (0 != groupDQuote || 0 != groupQuote || 0 != groupBrace || 0 != groupParen || 0 != groupBracket) {
            throw new InvalidPathException("Arguments to function: '"+ name + "' are not closed properly.");
        }
        return parameters;
    }

    private boolean isWhitespace(char c) {
        return (c == SPACE || c == TAB || c == LF || c == CR);
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
                int offset = wildCardIndex + 1;
                throw new InvalidPathException("Expected wildcard token to end with ']' on position " + offset);
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

        String expression = path.subSequence(expressionBeginIndex, expressionEndIndex).toString().trim();

        if ("*".equals(expression)) {
            return false;
        }

        //check valid chars
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (!isDigit(c) && c != COMMA && c != MINUS && c != SPLIT && c != SPACE) {
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
        boolean inEscape = false;
        boolean lastSignificantWasComma = false;

        while (path.inBounds(readPosition)) {
            char c = path.charAt(readPosition);

            if(inEscape){
                inEscape = false;
            } else if('\\' == c){
                inEscape = true;
            } else if (c == CLOSE_SQUARE_BRACKET && !inProperty) {
                if (lastSignificantWasComma){
                  fail("Found empty property at index "+readPosition);
                }
                break;
            } else if (c == potentialStringDelimiter) {
                if (inProperty) {
                    char nextSignificantChar = path.nextSignificantChar(readPosition);
                    if (nextSignificantChar != CLOSE_SQUARE_BRACKET && nextSignificantChar != COMMA) {
                        fail("Property must be separated by comma or Property must be terminated close square bracket at index "+readPosition);
                    }
                    endPosition = readPosition;
                    String prop = path.subSequence(startPosition, endPosition).toString();
                    properties.add(Utils.unescape(prop));
                    inProperty = false;
                } else {
                    startPosition = readPosition + 1;
                    inProperty = true;
                    lastSignificantWasComma = false;
                }
            } else if (c == COMMA){
                if (lastSignificantWasComma){
                    fail("Found empty property at index "+readPosition);
                }
                lastSignificantWasComma = true;
            }
            readPosition++;
        }

        if (inProperty){
            fail("Property has not been closed - missing closing " + potentialStringDelimiter);
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
