package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Filter;
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
    private static final char CLOSE_SQUARE_BRACKET = ']';
    private static final char OPEN_PARENTHESIS = '(';
    private static final char CLOSE_PARENTHESIS = ')';
    private static final char OPEN_OBJECT = '{';
    private static final char CLOSE_OBJECT = '}';
    private static final char OPEN_ARRAY = '[';
    private static final char CLOSE_ARRAY = ']';

    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';

    private static final char SPACE = ' ';
    private static final char PERIOD = '.';

    private static final char AND = '&';
    private static final char OR = '|';

    private static final char MINUS = '-';
    private static final char LT = '<';
    private static final char GT = '>';
    private static final char EQ = '=';
    private static final char TILDE = '~';
    private static final char TRUE = 't';
    private static final char FALSE = 'f';
    private static final char NULL = 'n';
    private static final char NOT = '!';
    private static final char PATTERN = '/';
    private static final char IGNORE_CASE = 'i';

    private CharacterIndex filter;

    public static Filter compile(String filterString) {
        FilterCompiler compiler = new FilterCompiler(filterString);
        return new CompiledFilter(compiler.compile());
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
        try {
            Stack<LogicalOperator> opsStack = new Stack<LogicalOperator>();
            Stack<ExpressionNode> expStack = new Stack<ExpressionNode>();

            int unbalancedParenthesis = 0;

            while (filter.skipBlanks().inBounds()) {
                int pos = filter.position();

                switch (filter.currentChar()) {
                    case OPEN_PARENTHESIS:
                        unbalancedParenthesis++;
                        filter.incrementPosition(1);
                        break;
                    case CLOSE_PARENTHESIS:
                        unbalancedParenthesis--;
                        filter.incrementPosition(1);
                        ExpressionNode expressionNode = expStack.pop();
                        if (!opsStack.isEmpty()) {
                            if (expStack.isEmpty()) {
                                throw new InvalidPathException("Expected expression on right hand side of operator " + opsStack.peek().getOperatorString() + " in filter " + filter);
                            }
                            ExpressionNode right = expStack.pop();
                            expressionNode = ExpressionNode.createExpressionNode(expressionNode, opsStack.pop(), right);
                            while (!opsStack.isEmpty()) {
                                expressionNode = ExpressionNode.createExpressionNode(expressionNode, opsStack.pop(), expStack.pop());
                            }
                        }
                        expStack.push(expressionNode);
                        break;
                    case NOT:
                        filter.incrementPosition(1);
                        break;
                    case OR:
                    case AND:
                        LogicalOperator operatorNode = readLogicalOperator();
                        opsStack.push(operatorNode);
                        break;
                    default:
                        if(expStack.size() > 0 && opsStack.isEmpty()){
                            throw new InvalidPathException("Expected logical operator (&&, ||) to follow expression " + expStack.peek().toString());
                        }
                        RelationalExpressionNode relationalExpressionNode = readExpression();
                        expStack.push(relationalExpressionNode);
                        break;
                }
                if (pos >= filter.position()) {
                    throw new InvalidPathException("Failed to parse filter " + filter.toString());
                }
            }
            if (unbalancedParenthesis != 0) {
                throw new InvalidPathException("Failed to parse filter. Parenthesis are not balanced. " + filter.toString());
            }

            Predicate predicate = expStack.pop();

            if (logger.isTraceEnabled()) logger.trace(predicate.toString());

            return predicate;
        } catch (InvalidPathException e){
            throw e;
        } catch (Exception e) {
            throw new InvalidPathException("Failed to parse filter: " + filter + ", error on position: " + filter.position() + ", char: " + filter.currentChar());
        }
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
            case SINGLE_QUOTE:  return readStringLiteral(SINGLE_QUOTE);
            case DOUBLE_QUOTE: return readStringLiteral(DOUBLE_QUOTE);
            case TRUE:  return readBooleanLiteral();
            case FALSE: return readBooleanLiteral();
            case MINUS: return readNumberLiteral();
            case NULL:  return readNullLiteral();
            case OPEN_OBJECT: return readJsonLiteral();
            case OPEN_ARRAY: return readJsonLiteral();
            case PATTERN: return readPattern();
            default:    return readNumberLiteral();
        }
    }

    private RelationalExpressionNode readExpression() {
        ValueNode left = readValueNode();
        if(expressionIsTerminated()) {
            ValueNode.PathNode pathNode = left.asPathNode();
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
        if(filter.currentChar() == NULL && filter.inBounds(filter.position() + 3)){
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

        assert openChar == OPEN_ARRAY || openChar == OPEN_OBJECT;

        char closeChar = openChar == OPEN_ARRAY ? CLOSE_ARRAY : CLOSE_OBJECT;

        int closingIndex = filter.indexOfMatchingCloseChar(filter.position(), openChar, closeChar, true, false);
        if (closingIndex == -1) {
            throw new InvalidPathException("String not closed. Expected " + SINGLE_QUOTE + " in " + filter);
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
            if(filter.inBounds(closingIndex+1) && filter.charAt(closingIndex+1) == IGNORE_CASE){
                closingIndex++;
            }
            filter.setPosition(closingIndex + 1);
        }
        CharSequence pattern = filter.subSequence(begin, filter.position());
        logger.trace("PatternNode from {} to {} -> [{}]", begin, filter.position(), pattern);
        return ValueNode.createPatternNode(pattern);
    }

    private ValueNode.StringNode readStringLiteral(char endChar) {
        int begin = filter.position();

        int closingSingleQuoteIndex = filter.nextIndexOfUnescaped(endChar);
        if (closingSingleQuoteIndex == -1) {
            throw new InvalidPathException("String literal does not have matching quotes. Expected " + endChar + " in " + filter);
        } else {
            filter.setPosition(closingSingleQuoteIndex + 1);
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
        int end = filter.currentChar() == TRUE ? filter.position() + 3 : filter.position() + 4;

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

    private ValueNode.PathNode readPath() {
        char previousSignificantChar = filter.previousSignificantChar();
        int begin = filter.position();

        filter.incrementPosition(1); //skip $ and @
        while (filter.inBounds()) {
            if (filter.currentChar() == OPEN_SQUARE_BRACKET) {
                int closingSquareBracketIndex = filter.indexOfMatchingCloseChar(filter.position(), OPEN_SQUARE_BRACKET, CLOSE_SQUARE_BRACKET, true, false);
                if (closingSquareBracketIndex == -1) {
                    throw new InvalidPathException("Square brackets does not match in filter " + filter);
                } else {
                    filter.setPosition(closingSquareBracketIndex + 1);
                }
            }
            boolean closingFunctionBracket = (filter.currentChar() == CLOSE_PARENTHESIS && currentCharIsClosingFunctionBracket(begin));
            boolean closingLogicalBracket  = (filter.currentChar() == CLOSE_PARENTHESIS && !closingFunctionBracket);

            if (!filter.inBounds() || isRelationalOperatorChar(filter.currentChar()) || filter.currentChar() == SPACE || closingLogicalBracket) {
                break;
            } else {
                filter.incrementPosition(1);
            }
        }

        boolean shouldExists = !(previousSignificantChar == NOT);
        CharSequence path = filter.subSequence(begin, filter.position());
        return ValueNode.createPathNode(path, false, shouldExists);
    }

    private boolean expressionIsTerminated(){
        char c = filter.currentChar();
        if(c == CLOSE_PARENTHESIS || isLogicalOperatorChar(c)){
            return true;
        }
        c = filter.nextSignificantChar();
        if(c == CLOSE_PARENTHESIS || isLogicalOperatorChar(c)){
            return true;
        }
        return false;
    }

    private boolean currentCharIsClosingFunctionBracket(int lowerBound){
        if(filter.currentChar() != CLOSE_PARENTHESIS){
            return false;
        }
        int idx = filter.indexOfPreviousSignificantChar();
        if(idx == -1 || filter.charAt(idx) != OPEN_PARENTHESIS){
            return false;
        }
        idx--;
        while(filter.inBounds(idx) && idx > lowerBound){
            if(filter.charAt(idx) == PERIOD){
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
        return c == LT || c == GT || c == EQ || c == TILDE || c == NOT;
    }

    private static final class CompiledFilter extends Filter {

        private final Predicate predicate;

        private CompiledFilter(Predicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean apply(Predicate.PredicateContext ctx) {
            return predicate.apply(ctx);
        }

        @Override
        public String toString() {
            String predicateString = predicate.toString();
            if(predicateString.startsWith("(")){
                return "[?" + predicateString + "]";
            } else {
                return "[?(" + predicateString + ")]";
            }
        }
    }
}
