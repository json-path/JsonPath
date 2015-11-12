package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.CharacterIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

public class FilterCompiler {
    private static final Logger logger = LoggerFactory.getLogger(FilterCompiler.class);

    private static final char DOC_CONTEXT = '$';
    private static final char EVAL_CONTEXT = '@';
    private static final char OPEN_SQUARE_BRACKET = '[';
    private static final char OPEN_BRACKET = '(';
    private static final char CLOSE_BRACKET = ')';
    private static final char SPACE = ' ';
    private static final char MINUS = '-';
    private static final char TICK = '\'';
    private static final char FUNCTION = '%';
    private static final char LT = '<';
    private static final char GT = '>';
    private static final char EQ = '=';
    private static final char TILDE = '~';
    private static final char TRUE = 't';
    private static final char FALSE = 'f';
    private static final char NULL = 'n';
    private static final char AND = '&';
    private static final char OR = '|';
    private static final char OBJECT_OPEN = '{';
    private static final char OBJECT_CLOSE = '}';
    private static final char ARRAY_OPEN = '[';
    private static final char ARRAY_CLOSE = ']';
    private static final char BANG = '!';
    private static final char PATTERN = '/';

    private CharacterIndex filter;

    public static Predicate compile(String filterString) {
        FilterCompiler compiler = new FilterCompiler(filterString);
        return compiler.compile();
    }

    private FilterCompiler(String filterString) {
        filterString = filterString.trim();
        if (!filterString.startsWith("[") || !filterString.endsWith("]")) {
            throw new InvalidPathException("Filter must start with '[' and end with ']'. " + filterString);
        }
        filterString = filterString.substring(1, filterString.length() - 1).trim();
        if (!filterString.startsWith("?")) {
            throw new InvalidPathException("Filter must start with '[?' and end with ']'. " + filterString);
        }
        filterString = filterString.substring(1).trim();
        if (!filterString.startsWith("(") || !filterString.endsWith(")")) {
            throw new InvalidPathException("Filter must start with '[?(' and end with ')]'. " + filterString);
        }

        filter = new CharacterIndex(filterString);
    }

    public Predicate compile() {

        Stack<LogicalOperator> opsStack  = new Stack<LogicalOperator>();
        Stack<ExpressionNode> expStack = new Stack<ExpressionNode>();

        int unbalancedBrackets = 0;

        while (filter.skipBlanks().inBounds()) {
            int pos = filter.position();

            switch (filter.currentChar()) {
                case OPEN_BRACKET:
                    unbalancedBrackets++;
                    filter.incrementPosition(1);
                    break;
                case CLOSE_BRACKET:
                    unbalancedBrackets--;
                    filter.incrementPosition(1);
                    while(!opsStack.isEmpty()){
                        expStack.push(ExpressionNode.createExpressionNode(expStack.pop(), opsStack.pop(), expStack.pop()));
                    }
                    break;
                case BANG:
                    filter.incrementPosition(1);
                    break;
                case OR:
                case AND:
                    LogicalOperator operatorNode = readLogicalOperator();
                    opsStack.push(operatorNode);
                    break;
                default:
                    RelationalExpressionNode relationalExpressionNode = readExpression();
                    expStack.push(relationalExpressionNode);
                    break;
            }
            if(pos >= filter.position()){
                throw new InvalidPathException("Failed to parse filter " + filter.toString());
            }
        }
        if(unbalancedBrackets > 0){
            throw new InvalidPathException("Failed to parse filter. Brackets are not balanced. " + filter.toString());
        }

        Predicate predicate = expStack.pop();
        logger.trace(predicate.toString());

        return predicate;
    }

    private ValueNode readValueNode() {
        switch (filter.skipBlanks().currentChar()) {
            case DOC_CONTEXT  : return readPath();
            case EVAL_CONTEXT : return readPath();
            default : return readLiteral();
        }
    }

    private ValueNode readLiteral(){
        switch (filter.skipBlanks().currentChar()){
            case TICK:  return readStringLiteral();
            case TRUE:  return readBooleanLiteral();
            case FALSE: return readBooleanLiteral();
            case MINUS: return readNumberLiteral();
            case NULL:  return readNullLiteral();
            case OBJECT_OPEN: return readJsonLiteral();
            case ARRAY_OPEN: return readJsonLiteral();
            case PATTERN: return readPattern();
            default:    return readNumberLiteral();
        }
    }

    private RelationalExpressionNode readExpression() {
        ValueNode left = readValueNode();
        if(expressionIsTerminated()) {
            PathNode pathNode = left.asPathNode();
            left = pathNode.asExistsCheck(pathNode.shouldExists());
            RelationalOperator operator = RelationalOperator.EXISTS;
            ValueNode right = left.asPathNode().shouldExists() ? ValueNode.TRUE : ValueNode.FALSE;
            return new RelationalExpressionNode(left, operator, right);
        } else {
            RelationalOperator operator = readRelationalOperator();
            ValueNode right = readValueNode();
            return new RelationalExpressionNode(left, operator, right);
        }
    }

    private LogicalOperator readLogicalOperator(){
        int begin = filter.skipBlanks().position();
        int end = begin+1;

        if(!filter.inBounds(end)){
            throw new InvalidPathException("Expected boolean literal");
        }
        CharSequence logicalOperator = filter.subSequence(begin, end+1);
        if(!logicalOperator.equals("||") && !logicalOperator.equals("&&")){
            throw new InvalidPathException("Expected logical operator");
        }
        filter.incrementPosition(logicalOperator.length());
        logger.trace("LogicalOperator from {} to {} -> [{}]", begin, end, logicalOperator);

        return LogicalOperator.fromString(logicalOperator.toString());
    }

    private RelationalOperator readRelationalOperator() {
        int begin = filter.skipBlanks().position();

        if(isRelationalOperatorChar(filter.currentChar())){
            while (filter.inBounds() && isRelationalOperatorChar(filter.currentChar())) {
                filter.incrementPosition(1);
            }
        } else {
            while (filter.inBounds() && filter.currentChar() != SPACE) {
                filter.incrementPosition(1);
            }
        }

        CharSequence operator = filter.subSequence(begin, filter.position());
        logger.trace("Operator from {} to {} -> [{}]", begin, filter.position()-1, operator);
        return RelationalOperator.fromString(operator.toString());
    }

    private ValueNode.NullNode readNullLiteral() {
        int begin = filter.position();
        if(filter.currentChar() == 'n' && filter.inBounds(filter.position() + 3)){
            CharSequence nullValue = filter.subSequence(filter.position(), filter.position() + 4);
            if("null".equals(nullValue.toString())){
                logger.trace("NullLiteral from {} to {} -> [{}]", begin, filter.position()+3, nullValue);
                filter.incrementPosition(nullValue.length());
                return ValueNode.createNullNode();
            }
        }
        throw new InvalidPathException("Expected <null> value");
    }

    private ValueNode.JsonNode readJsonLiteral(){
        int begin = filter.position();

        char openChar = filter.currentChar();

        assert openChar == ARRAY_OPEN || openChar == OBJECT_OPEN;

        char closeChar = openChar == ARRAY_OPEN ? ARRAY_CLOSE : OBJECT_CLOSE;

        int closingIndex = filter.indexOfMatchingCloseChar(filter.position(), openChar, closeChar, true, false);
        if (closingIndex == -1) {
            throw new InvalidPathException("String not closed. Expected " + TICK + " in " + filter);
        } else {
            filter.setPosition(closingIndex + 1);
        }
        CharSequence json = filter.subSequence(begin, filter.position());
        logger.trace("JsonLiteral from {} to {} -> [{}]", begin, filter.position(), json);
        return ValueNode.createJsonNode(json);

    }

    private ValueNode.PatternNode readPattern() {
        int begin = filter.position();
        int closingIndex = filter.nextIndexOfUnescaped(PATTERN);
        if (closingIndex == -1) {
            throw new InvalidPathException("Pattern not closed. Expected " + PATTERN + " in " + filter);
        } else {
            if(filter.inBounds(closingIndex+1) && filter.charAt(closingIndex+1) == 'i'){
                closingIndex++;
            }
            filter.setPosition(closingIndex + 1);
        }
        CharSequence pattern = filter.subSequence(begin, filter.position());
        logger.trace("PatternNode from {} to {} -> [{}]", begin, filter.position(), pattern);
        return ValueNode.createPatternNode(pattern);
    }

    private ValueNode.StringNode readStringLiteral() {
        int begin = filter.position();

        int closingTickIndex = filter.nextIndexOfUnescaped(TICK);
        if (closingTickIndex == -1) {
            throw new InvalidPathException("String not closed. Expected " + TICK + " in " + filter);
        } else {
            filter.setPosition(closingTickIndex + 1);
        }
        CharSequence stringLiteral = filter.subSequence(begin, filter.position());
        logger.trace("StringLiteral from {} to {} -> [{}]", begin, filter.position(), stringLiteral);
        return ValueNode.createStringNode(stringLiteral, true);
    }

    private ValueNode.NumberNode readNumberLiteral() {
        int begin = filter.position();

        while (filter.inBounds() && filter.isNumberCharacter(filter.position())) {
            filter.incrementPosition(1);
        }
        CharSequence numberLiteral = filter.subSequence(begin, filter.position());
        logger.trace("NumberLiteral from {} to {} -> [{}]", begin, filter.position(), numberLiteral);
        return ValueNode.createNumberNode(numberLiteral);
    }

    private ValueNode.BooleanNode readBooleanLiteral() {
        int begin = filter.position();
        int end = filter.currentChar() == 't' ? filter.position() + 3 : filter.position() + 4;

        if(!filter.inBounds(end)){
            throw new InvalidPathException("Expected boolean literal");
        }
        CharSequence boolValue = filter.subSequence(begin, end+1);
        if(!boolValue.equals("true") && !boolValue.equals("false")){
            throw new InvalidPathException("Expected boolean literal");
        }
        filter.incrementPosition(boolValue.length());
        logger.trace("BooleanLiteral from {} to {} -> [{}]", begin, end, boolValue);

        return ValueNode.createBooleanNode(boolValue);
    }

    private PathNode readPath() {
        char previousSignificantChar = filter.previousSignificantChar();
        int begin = filter.position();

        filter.incrementPosition(1); //skip $ and @
        while (filter.inBounds()) {
            if (filter.currentChar() == OPEN_SQUARE_BRACKET) {
                int closingSquareBracketIndex = filter.indexOfClosingSquareBracket(filter.position());
                if (closingSquareBracketIndex == -1) {
                    throw new InvalidPathException("Square brackets does not match in filter " + filter);
                } else {
                    filter.setPosition(closingSquareBracketIndex + 1);
                }
            }
            boolean closingLogicalBracket = (filter.currentChar() == CLOSE_BRACKET && !currentCharIsClosingFunctionBracket(begin));
            if (!filter.inBounds() || isRelationalOperatorChar(filter.currentChar()) || filter.currentChar() == SPACE || closingLogicalBracket) {
                break;
            } else {
                filter.incrementPosition(1);
            }
        }

        boolean shouldExists = !(previousSignificantChar == BANG);
        CharSequence path = filter.subSequence(begin, filter.position());
        return new PathNode(path, false, shouldExists);
    }

    private boolean expressionIsTerminated(){
        char c = filter.currentChar();
        if(c == CLOSE_BRACKET || isLogicalOperatorChar(c)){
            return true;
        }
        c = filter.nextSignificantChar();
        if(c == CLOSE_BRACKET || isLogicalOperatorChar(c)){
            return true;
        }
        return false;
    }

    private boolean currentCharIsClosingFunctionBracket(int lowerBound){
        if(filter.currentChar() != CLOSE_BRACKET){
            return false;
        }
        int idx = filter.indexOfPreviousSignificantChar();
        if(idx == -1 || filter.charAt(idx) != OPEN_BRACKET){
            return false;
        }
        idx--;
        while(filter.inBounds(idx) && idx > lowerBound){
            if(filter.charAt(idx) == FUNCTION){
                return true;
            }
            idx--;
        }
        return false;
    }

    private boolean isLogicalOperatorChar(char c) {
        return c == AND || c == OR;
    }
    private boolean isRelationalOperatorChar(char c) {
        return c == LT || c == GT || c == EQ || c == TILDE || c == BANG;
    }
}
