package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Predicate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.internal.filter.ValueNodes.PatternNode;
import static com.jayway.jsonpath.internal.filter.ValueNodes.ValueListNode;

public class EvaluatorFactory {

    private static final Map<RelationalOperator, Evaluator> evaluators = new HashMap<RelationalOperator, Evaluator>();

    static {
        evaluators.put(RelationalOperator.EXISTS, new ExistsEvaluator());
        evaluators.put(RelationalOperator.NE, new NotEqualsEvaluator());
        evaluators.put(RelationalOperator.TSNE, new TypeSafeNotEqualsEvaluator());
        evaluators.put(RelationalOperator.EQ, new EqualsEvaluator());
        evaluators.put(RelationalOperator.TSEQ, new TypeSafeEqualsEvaluator());
        evaluators.put(RelationalOperator.LT, new LessThanEvaluator());
        evaluators.put(RelationalOperator.LTE, new LessThanEqualsEvaluator());
        evaluators.put(RelationalOperator.GT, new GreaterThanEvaluator());
        evaluators.put(RelationalOperator.GTE, new GreaterThanEqualsEvaluator());
        evaluators.put(RelationalOperator.REGEX, new RegexpEvaluator());
        evaluators.put(RelationalOperator.SIZE, new SizeEvaluator());
        evaluators.put(RelationalOperator.EMPTY, new EmptyEvaluator());
        evaluators.put(RelationalOperator.IN, new InEvaluator());
        evaluators.put(RelationalOperator.NIN, new NotInEvaluator());
        evaluators.put(RelationalOperator.ALL, new AllEvaluator());
        evaluators.put(RelationalOperator.CONTAINS, new ContainsEvaluator());
        evaluators.put(RelationalOperator.MATCHES, new PredicateMatchEvaluator());
        evaluators.put(RelationalOperator.TYPE, new TypeEvaluator());
        evaluators.put(RelationalOperator.SUBSETOF, new SubsetOfEvaluator());
        evaluators.put(RelationalOperator.ANYOF, new AnyOfEvaluator());
        evaluators.put(RelationalOperator.NONEOF, new NoneOfEvaluator());
        evaluators.put(RelationalOperator.GTALL, new GreaterThanAllEvaluator());
        evaluators.put(RelationalOperator.GTEALL, new GreaterThanEqualsAllEvaluator());
        evaluators.put(RelationalOperator.GTANY, new GreaterThanAnyEvaluator());
        evaluators.put(RelationalOperator.GTEANY, new GreaterThanEqualsAnyEvaluator());
        evaluators.put(RelationalOperator.LTALL, new LessThanAllEvaluator());
        evaluators.put(RelationalOperator.LTEALL, new LessThanEqualsAllEvaluator());
        evaluators.put(RelationalOperator.LTANY, new LessThanAnyEvaluator());
        evaluators.put(RelationalOperator.LTEANY, new LessThanEqualsAnyEvaluator());
        evaluators.put(RelationalOperator.DATEEQ, new DateMatchEvaluator());
        evaluators.put(RelationalOperator.DAYEQ, new DayMatchEvaluator());
        evaluators.put(RelationalOperator.DAYIN, new DayInEvaluator());
        evaluators.put(RelationalOperator.MONTHEQ, new MonthMatchEvaluator());
        evaluators.put(RelationalOperator.MONTHIN, new MonthInEvaluator());
        evaluators.put(RelationalOperator.YEAREQ, new YearMatchEvaluator());
        evaluators.put(RelationalOperator.YEARIN, new YearInEvaluator());
        evaluators.put(RelationalOperator.BEFORE, new BeforeEvaluator());
        evaluators.put(RelationalOperator.AFTER, new AfterEvaluator());
        evaluators.put(RelationalOperator.HOUREQ, new HourMatchEvaluator());
        evaluators.put(RelationalOperator.HOURIN, new HourInEvaluator());
        evaluators.put(RelationalOperator.TIMEBEFORE, new TimeBeforeEvaluator());
        evaluators.put(RelationalOperator.TIMEAFTER, new TimeAfterEvaluator());
        evaluators.put(RelationalOperator.NOTCONTAINS, new NotContainsEvaluator());
        evaluators.put(RelationalOperator.ALLMATCH, new AllMatchEvaluator());
        evaluators.put(RelationalOperator.ANYMATCH, new AnyMatchEvaluator());
        evaluators.put(RelationalOperator.NONEMATCH, new NoneMatchEvaluator());
        evaluators.put(RelationalOperator.EXACTMATCH, new ExactMatchEvaluator());
        evaluators.put(RelationalOperator.WINDOWIN, new WindowInEvaluator());
        evaluators.put(RelationalOperator.WINDOWOUT, new WindowOutEvaluator());
        evaluators.put(RelationalOperator.WINDOWTIMEIN, new WindowTimeInEvaluator());
        evaluators.put(RelationalOperator.WINDOWTIMEOUT, new WindowTimeOutEvaluator());
    }

    public static Evaluator createEvaluator(RelationalOperator operator) {
        return evaluators.get(operator);
    }

    private static class ExistsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (!left.isBooleanNode() && !right.isBooleanNode()) {
                throw new JsonPathException("Failed to evaluate exists expression");
            }
            return left.asBooleanNode().getBoolean() == right.asBooleanNode().getBoolean();
        }
    }

    private static class NotEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return !evaluators.get(RelationalOperator.EQ).evaluate(left, right, ctx);
        }
    }

    private static class TypeSafeNotEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return !evaluators.get(RelationalOperator.TSEQ).evaluate(left, right, ctx);
        }
    }

    private static class EqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (left.isJsonNode() && right.isJsonNode()) {
                return left.asJsonNode().equals(right.asJsonNode(), ctx);
            } else {
                return left.equals(right);
            }
        }
    }

    private static class TypeSafeEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (!left.getClass().equals(right.getClass())) {
                return false;
            }
            return evaluators.get(RelationalOperator.EQ).evaluate(left, right, ctx);
        }
    }

    private static class TypeEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return right.asClassNode().getClazz() == left.type(ctx);
        }
    }

    private static class LessThanEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (left.isNumberNode() && right.isNumberNode()) {
                return left.asNumberNode().getNumber().compareTo(right.asNumberNode().getNumber()) < 0;
            }
            if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) < 0;
            }
            if (left.isOffsetDateTimeNode() && right.isOffsetDateTimeNode()) { //workaround for issue: https://github.com/json-path/JsonPath/issues/613
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) < 0;
            }
            return false;
        }
    }

    private static class LessThanAllEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode leftListNode = left.isValueListNode() ? left.asValueListNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (leftListNode == null) {
                leftListNode = convertJsonNodeToListNode(left, ctx);
            }
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            if (leftListNode.getNodes().get(0).isNumberNode() && rightListNode.getNodes().get(0).isNumberNode()) {
                BigDecimal smallest = leftListNode.getNodes().stream().map(node ->
                        node.asNumberNode().getNumber()).min(Comparator.naturalOrder()).get();
                return rightListNode.getNodes().stream().allMatch(node -> node.asNumberNode()
                        .getNumber().compareTo(smallest) < 0);
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn = node.isNumberNode() || node.isStringNode() ?
                    new ValueListNode(Collections.singleton(node.asNumberNode())) :
                    node.asJsonNode().asValueListNode(ctx);
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class LessThanAnyEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode leftListNode = left.isValueListNode() ? left.asValueListNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (leftListNode == null) {
                leftListNode = convertJsonNodeToListNode(left, ctx);
            }
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            if (leftListNode.getNodes().get(0).isNumberNode() && rightListNode.getNodes().get(0).isNumberNode()) {
                boolean res = false;
                for (ValueNode field : leftListNode.getNodes()) {
                    for (ValueNode value : rightListNode.getNodes()) {
                        if (field.asNumberNode().getNumber().compareTo(value.asNumberNode().getNumber()) < 0) {
                            res = true;
                            break;
                        }
                    }
                }
                return res;
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn = node.isNumberNode() || node.isStringNode() ?
                    new ValueListNode(Collections.singleton(node.asNumberNode())) :
                    node.asJsonNode().asValueListNode(ctx);
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class LessThanEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (left.isNumberNode() && right.isNumberNode()) {
                return left.asNumberNode().getNumber().compareTo(right.asNumberNode().getNumber()) <= 0;
            }
            if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) <= 0;
            }
            if (left.isOffsetDateTimeNode() && right.isOffsetDateTimeNode()) { //workaround for issue: https://github.com/json-path/JsonPath/issues/613
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) <= 0;
            }
            return false;
        }
    }

    private static class LessThanEqualsAllEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode leftListNode = left.isValueListNode() ? left.asValueListNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (leftListNode == null) {
                leftListNode = convertJsonNodeToListNode(left, ctx);
            }
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            if (leftListNode.getNodes().get(0).isNumberNode() && rightListNode.getNodes().get(0).isNumberNode()) {
                BigDecimal smallest = leftListNode.getNodes().stream().map(node ->
                        node.asNumberNode().getNumber()).min(Comparator.naturalOrder()).get();
                return rightListNode.getNodes().stream().allMatch(node -> node.asNumberNode()
                        .getNumber().compareTo(smallest) <= 0);
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn = node.isNumberNode() || node.isStringNode() ?
                    new ValueListNode(Collections.singleton(node.asNumberNode())) :
                    node.asJsonNode().asValueListNode(ctx);
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class LessThanEqualsAnyEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode leftListNode = left.isValueListNode() ? left.asValueListNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (leftListNode == null) {
                leftListNode = convertJsonNodeToListNode(left, ctx);
            }
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            if (leftListNode.getNodes().get(0).isNumberNode() && rightListNode.getNodes().get(0).isNumberNode()) {
                boolean res = false;
                for (ValueNode field : leftListNode.getNodes()) {
                    for (ValueNode value : rightListNode.getNodes()) {
                        if (field.asNumberNode().getNumber().compareTo(value.asNumberNode().getNumber()) <= 0) {
                            res = true;
                            break;
                        }
                    }
                }
                return res;
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn = node.isNumberNode() || node.isStringNode() ?
                    new ValueListNode(Collections.singleton(node.asNumberNode())) :
                    node.asJsonNode().asValueListNode(ctx);
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class GreaterThanEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (left.isNumberNode() && right.isNumberNode()) {
                return left.asNumberNode().getNumber().compareTo(right.asNumberNode().getNumber()) > 0;
            } else if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) > 0;
            } else if (left.isOffsetDateTimeNode() && right.isOffsetDateTimeNode()) { //workaround for issue: https://github.com/json-path/JsonPath/issues/613
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) > 0;
            }
            return false;
        }
    }

    private static class GreaterThanAllEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode leftListNode = left.isValueListNode() ? left.asValueListNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (leftListNode == null) {
                leftListNode = convertJsonNodeToListNode(left, ctx);
            }
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            if (leftListNode.getNodes().get(0).isNumberNode() && rightListNode.getNodes().get(0).isNumberNode()) {
                BigDecimal largest = leftListNode.getNodes().stream().map(node ->
                        node.asNumberNode().getNumber()).max(Comparator.naturalOrder()).get();
                return rightListNode.getNodes().stream().allMatch(node -> node.asNumberNode()
                        .getNumber().compareTo(largest) < 0);
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn = node.isNumberNode() || node.isStringNode() ?
                    new ValueListNode(Collections.singleton(node.asNumberNode())) :
                    node.asJsonNode().asValueListNode(ctx);
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class GreaterThanAnyEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode leftListNode = left.isValueListNode() ? left.asValueListNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (leftListNode == null) {
                leftListNode = convertJsonNodeToListNode(left, ctx);
            }
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            if (leftListNode.getNodes().get(0).isNumberNode() && rightListNode.getNodes().get(0).isNumberNode()) {
                boolean res = false;
                for (ValueNode field : leftListNode.getNodes()) {
                    for (ValueNode value : rightListNode.getNodes()) {
                        if (field.asNumberNode().getNumber().compareTo(value.asNumberNode().getNumber()) > 0) {
                            res = true;
                            break;
                        }
                    }
                }
                return res;
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn = node.isNumberNode() || node.isStringNode() ?
                    new ValueListNode(Collections.singleton(node.asNumberNode())) :
                    node.asJsonNode().asValueListNode(ctx);
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class GreaterThanEqualsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (left.isNumberNode() && right.isNumberNode()) {
                return left.asNumberNode().getNumber().compareTo(right.asNumberNode().getNumber()) >= 0;
            } else if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().getString().compareTo(right.asStringNode().getString()) >= 0;
            } else if (left.isOffsetDateTimeNode() && right.isOffsetDateTimeNode()) { //workaround for issue: https://github.com/json-path/JsonPath/issues/613
                return left.asOffsetDateTimeNode().getDate().compareTo(right.asOffsetDateTimeNode().getDate()) >= 0;
            }
            return false;
        }
    }

    private static class GreaterThanEqualsAllEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode leftListNode = left.isValueListNode() ? left.asValueListNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (leftListNode == null) {
                leftListNode = convertJsonNodeToListNode(left, ctx);
            }
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            if (leftListNode.getNodes().get(0).isNumberNode() && rightListNode.getNodes().get(0).isNumberNode()) {
                BigDecimal largest = leftListNode.getNodes().stream().map(node ->
                        node.asNumberNode().getNumber()).max(Comparator.naturalOrder()).get();
                return rightListNode.getNodes().stream().allMatch(node -> node.asNumberNode()
                        .getNumber().compareTo(largest) <= 0);
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn = node.isNumberNode() || node.isStringNode() ?
                    new ValueListNode(Collections.singleton(node.asNumberNode())) :
                    node.asJsonNode().asValueListNode(ctx);
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class GreaterThanEqualsAnyEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode leftListNode = left.isValueListNode() ? left.asValueListNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (leftListNode == null) {
                leftListNode = convertJsonNodeToListNode(left, ctx);
            }
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            if (leftListNode.getNodes().get(0).isNumberNode() && rightListNode.getNodes().get(0).isNumberNode()) {
                boolean res = false;
                for (ValueNode field : leftListNode.getNodes()) {
                    for (ValueNode value : rightListNode.getNodes()) {
                        if (field.asNumberNode().getNumber().compareTo(value.asNumberNode().getNumber()) >= 0) {
                            res = true;
                            break;
                        }
                    }
                }
                return res;
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn = node.isNumberNode() || node.isStringNode() ?
                    new ValueListNode(Collections.singleton(node.asNumberNode())) :
                    node.asJsonNode().asValueListNode(ctx);
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class SizeEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (!right.isNumberNode()) {
                return false;
            }
            int expectedSize = right.asNumberNode().getNumber().intValue();

            if (left.isStringNode()) {
                return left.asStringNode().length() == expectedSize;
            } else if (left.isJsonNode()) {
                return left.asJsonNode().length(ctx) == expectedSize;
            }
            return false;
        }
    }

    private static class EmptyEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (left.isStringNode()) {
                return left.asStringNode().isEmpty() == right.asBooleanNode().getBoolean();
            } else if (left.isJsonNode()) {
                return left.asJsonNode().isEmpty(ctx) == right.asBooleanNode().getBoolean();
            }
            return false;
        }
    }

    private static class InEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode;
            if (right.isJsonNode()) {
                ValueNode vn = right.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    valueListNode = vn.asValueListNode();
                }
            } else {
                valueListNode = right.asValueListNode();
            }
            return valueListNode.contains(left);
        }
    }

    private static class NotInEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return !evaluators.get(RelationalOperator.IN).evaluate(left, right, ctx);
        }
    }

    private static class AllEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode requiredValues = right.asValueListNode();

            if (left.isJsonNode()) {
                ValueNode valueNode = left.asJsonNode().asValueListNode(ctx); //returns UndefinedNode if conversion is not possible
                if (valueNode.isValueListNode()) {
                    ValueListNode shouldContainAll = valueNode.asValueListNode();
                    for (ValueNode required : requiredValues) {
                        if (!shouldContainAll.contains(required)) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
    }

    private static class ContainsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (left.isStringNode() && right.isStringNode()) {
                return left.asStringNode().contains(right.asStringNode().getString());
            } else if (left.isJsonNode()) {
                ValueNode valueNode = left.asJsonNode().asValueListNode(ctx);
                if (valueNode.isUndefinedNode()) return false;
                else {
                    return valueNode.asValueListNode().contains(right);
                }
            }
            return false;
        }
    }

    private static class NotContainsEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (left.isStringNode() && right.isStringNode()) {
                return !(left.asStringNode().contains(right.asStringNode().getString()));
            } else if (left.isJsonNode()) {
                ValueNode valueNode = left.asJsonNode().asValueListNode(ctx);
                if (valueNode.isUndefinedNode()) return false;
                else {
                    return !(valueNode.asValueListNode().contains(right));
                }
            }
            return false;
        }
    }

    private static class PredicateMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return right.asPredicateNode().getPredicate().apply(ctx);
        }
    }

    private static class RegexpEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            if (!(left.isPatternNode() ^ right.isPatternNode())) {
                return false;
            }

            if (left.isPatternNode()) {
                if (right.isValueListNode() || (right.isJsonNode() && right.asJsonNode().isArray(ctx))) {
                    return matchesAny(left.asPatternNode(), right.asJsonNode().asValueListNode(ctx));
                } else {
                    return matches(left.asPatternNode(), getInput(right));
                }
            } else {
                if (left.isValueListNode() || (left.isJsonNode() && left.asJsonNode().isArray(ctx))) {
                    return matchesAny(right.asPatternNode(), left.asJsonNode().asValueListNode(ctx));
                } else {
                    return matches(right.asPatternNode(), getInput(left));
                }
            }
        }

        private boolean matches(PatternNode patternNode, String inputToMatch) {
            return patternNode.getCompiledPattern().matcher(inputToMatch).matches();
        }

        private boolean matchesAny(PatternNode patternNode, ValueNode valueNode) {
            if (!valueNode.isValueListNode()) {
                return false;
            }

            ValueListNode listNode = valueNode.asValueListNode();
            Pattern pattern = patternNode.getCompiledPattern();

            for (Iterator<ValueNode> it = listNode.iterator(); it.hasNext(); ) {
                String input = getInput(it.next());
                if (pattern.matcher(input).matches()) {
                    return true;
                }
            }
            return false;
        }

        private String getInput(ValueNode valueNode) {
            String input = "";

            if (valueNode.isStringNode() || valueNode.isNumberNode()) {
                input = valueNode.asStringNode().getString();
            } else if (valueNode.isBooleanNode()) {
                input = valueNode.asBooleanNode().toString();
            }

            return input;
        }
    }

    private static class SubsetOfEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode rightValueListNode;
            if (right.isJsonNode()) {
                ValueNode vn = right.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    rightValueListNode = vn.asValueListNode();
                }
            } else {
                rightValueListNode = right.asValueListNode();
            }
            ValueListNode leftValueListNode;
            if (left.isJsonNode()) {
                ValueNode vn = left.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    leftValueListNode = vn.asValueListNode();
                }
            } else {
                leftValueListNode = left.asValueListNode();
            }
            return leftValueListNode.subsetof(rightValueListNode);
        }
    }

    private static class AnyOfEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode rightValueListNode;
            if (right.isJsonNode()) {
                ValueNode vn = right.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    rightValueListNode = vn.asValueListNode();
                }
            } else {
                rightValueListNode = right.asValueListNode();
            }
            ValueListNode leftValueListNode;
            if (left.isJsonNode()) {
                ValueNode vn = left.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    leftValueListNode = vn.asValueListNode();
                }
            } else {
                leftValueListNode = left.asValueListNode();
            }

            for (ValueNode leftValueNode : leftValueListNode) {
                for (ValueNode rightValueNode : rightValueListNode) {
                    if (leftValueNode.equals(rightValueNode)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class NoneOfEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueListNode rightValueListNode;
            if (right.isJsonNode()) {
                ValueNode vn = right.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    rightValueListNode = vn.asValueListNode();
                }
            } else {
                rightValueListNode = right.asValueListNode();
            }
            ValueListNode leftValueListNode;
            if (left.isJsonNode()) {
                ValueNode vn = left.asJsonNode().asValueListNode(ctx);
                if (vn.isUndefinedNode()) {
                    return false;
                } else {
                    leftValueListNode = vn.asValueListNode();
                }
            } else {
                leftValueListNode = left.asValueListNode();
            }

            for (ValueNode leftValueNode : leftValueListNode) {
                for (ValueNode rightValueNode : rightValueListNode) {
                    if (leftValueNode.equals(rightValueNode)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static class DateMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime compDate = left.asDateNode().getDate();
            ZonedDateTime evalDate = right.asDateNode().getDate().withZoneSameLocal(compDate.getZone());
            return compDate.toLocalDate().compareTo(evalDate.toLocalDate()) == 0;
        }
    }

    private static class MonthMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            try {
                ZonedDateTime compDate = left.asDateNode().getDate();
                ZonedDateTime evalDate = right.asDateNode().getDate().withZoneSameLocal(compDate.getZone());
                return compDate.getMonth().compareTo(evalDate.getMonth()) == 0;
            } catch (InvalidPathException e) {
                Month month;
                if (right.isStringNode()) {
                    month = Month.valueOf(right.asStringNode().getString());
                } else {
                    month = Month.of(right.asNumberNode().getNumber().intValue());
                }
                return left.asDateNode().getDate().getMonth() == month;
            }
        }
    }

    private static class MonthInEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueNodes.DateNode leftListNode = left.isDateNode() ? left.asDateNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            ZonedDateTime compDate = leftListNode.getDate();
            int month = compDate.getMonthValue();
            if (rightListNode.getNodes().get(0).isDateNode()) {
                return rightListNode.getNodes().stream().anyMatch(node ->
                        node.asDateNode().getDate().withZoneSameLocal(compDate.getZone()).getMonthValue() == month);
            } else if (rightListNode.getNodes().get(0).isStringNode()) {
                return rightListNode.getNodes().stream().anyMatch(node ->
                        Month.valueOf(node.asStringNode().getString()).getValue() == month);
            } else if (rightListNode.getNodes().get(0).isNumberNode()) {
                return rightListNode.getNodes().stream().anyMatch(node ->
                        Month.of(node.asNumberNode().getNumber().intValue()).getValue() == month);
            }
            return false;
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn;
            try {
                vn = new ValueListNode(Collections.singleton(node.asDateNode()));
            } catch (InvalidPathException e) {
                if (node.isStringNode()) {
                    vn = new ValueListNode(Collections.singleton(node.asStringNode()));
                } else if (node.isJsonNode()) {
                    vn = node.asJsonNode().asValueListNode(ctx);
                } else {
                    vn = new ValueListNode(Collections.singleton(node.asNumberNode()));
                }
            }
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class DayMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            try {
                ZonedDateTime compDate = left.asDateNode().getDate();
                ZonedDateTime evalDate = right.asDateNode().getDate().withZoneSameLocal(compDate.getZone());
                return compDate.getDayOfMonth() == evalDate.getDayOfMonth();
            } catch (InvalidPathException e) {
                return left.asDateNode().getDate().getDayOfMonth() == right.asNumberNode().getNumber().intValue();
            }
        }
    }

    private static class DayInEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueNodes.DateNode leftListNode = left.isDateNode() ? left.asDateNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            ZonedDateTime compDate = leftListNode.getDate();
            int day = compDate.getDayOfMonth();
            if (rightListNode.getNodes().get(0).isDateNode()) {
                return rightListNode.getNodes().stream().anyMatch(node ->
                        node.asDateNode().getDate().withZoneSameLocal(compDate.getZone()).getDayOfMonth() == day);
            }
            return rightListNode.getNodes().stream()
                    .anyMatch(node -> node.asNumberNode().getNumber().intValue() == day);
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn;
            try {
                vn = new ValueListNode(Collections.singleton(node.asDateNode()));
            } catch (InvalidPathException e) {
                if (node.isStringNode()) {
                    vn = new ValueListNode(Collections.singleton(node.asStringNode()));
                } else if (node.isJsonNode()) {
                    vn = node.asJsonNode().asValueListNode(ctx);
                } else {
                    vn = new ValueListNode(Collections.singleton(node.asNumberNode()));
                }
            }
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class YearMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            try {
                ZonedDateTime compDate = left.asDateNode().getDate();
                ZonedDateTime evalDate = right.asDateNode().getDate().withZoneSameLocal(compDate.getZone());
                return compDate.getYear() == evalDate.getYear();
            } catch (InvalidPathException e) {
                return left.asDateNode().getDate().getYear() == right.asNumberNode().getNumber().intValue();
            }
        }
    }

    private static class YearInEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueNodes.DateNode leftListNode = left.isDateNode() ? left.asDateNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            ZonedDateTime compDate = leftListNode.getDate();
            int year = compDate.getYear();
            if (rightListNode.getNodes().get(0).isDateNode()) {
                return rightListNode.getNodes().stream().anyMatch(node ->
                        node.asDateNode().getDate().withZoneSameLocal(compDate.getZone()).getYear() == year);
            }
            return rightListNode.getNodes().stream()
                    .anyMatch(node -> node.asNumberNode().getNumber().intValue() == year);
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn;
            try {
                vn = new ValueListNode(Collections.singleton(node.asDateNode()));
            } catch (InvalidPathException e) {
                if (node.isStringNode()) {
                    vn = new ValueListNode(Collections.singleton(node.asStringNode()));
                } else if (node.isJsonNode()) {
                    vn = node.asJsonNode().asValueListNode(ctx);
                } else {
                    vn = new ValueListNode(Collections.singleton(node.asNumberNode()));
                }
            }
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class BeforeEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime compDate = left.asDateNode().getDate();
            ZonedDateTime evalDate = right.asDateNode().getDate().withZoneSameLocal(compDate.getZone());
            return compDate.isBefore(evalDate);
        }
    }

    private static class AfterEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime compDate = left.asDateNode().getDate();
            ZonedDateTime evalDate = right.asDateNode().getDate().withZoneSameLocal(compDate.getZone());
            return compDate.isAfter(evalDate);
        }
    }

    private static class HourMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            try {
                ZonedDateTime compDate = left.asDateNode().getDate();
                ZonedDateTime evalDate = right.asDateNode().getDate().withZoneSameLocal(compDate.getZone());
                return compDate.getHour() == evalDate.getHour();
            } catch (InvalidPathException e) {
                return left.asDateNode().getDate().getHour() == right.asNumberNode().getNumber().intValue();
            }
        }
    }

    private static class HourInEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ValueNodes.DateNode leftListNode = left.isDateNode() ? left.asDateNode() : null;
            ValueListNode rightListNode = right.isValueListNode() ? right.asValueListNode() : null;
            if (rightListNode == null) {
                rightListNode = convertJsonNodeToListNode(right, ctx);
            }
            if (leftListNode == null || rightListNode == null) {
                return false;
            }
            ZonedDateTime compDate = leftListNode.getDate();
            int hour = compDate.getHour();
            if (rightListNode.getNodes().get(0).isDateNode()) {
                return rightListNode.getNodes().stream().anyMatch(node ->
                        node.asDateNode().getDate().withZoneSameLocal(compDate.getZone()).getHour() == hour);
            }
            return rightListNode.getNodes().stream()
                    .anyMatch(node -> node.asNumberNode().getNumber().intValue() == hour);
        }

        private ValueListNode convertJsonNodeToListNode(ValueNode node, Predicate.PredicateContext ctx) {
            ValueListNode valueListNode = null;
            ValueNode vn;
            try {
                vn = new ValueListNode(Collections.singleton(node.asDateNode()));
            } catch (InvalidPathException e) {
                if (node.isStringNode()) {
                    vn = new ValueListNode(Collections.singleton(node.asStringNode()));
                } else if (node.isJsonNode()) {
                    vn = node.asJsonNode().asValueListNode(ctx);
                } else {
                    vn = new ValueListNode(Collections.singleton(node.asNumberNode()));
                }
            }
            if (!vn.isUndefinedNode()) {
                valueListNode = vn.asValueListNode();
            }
            return valueListNode;
        }
    }

    private static class TimeBeforeEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime date = left.asDateNode().getDate();
            String[] time = right.asStringNode().getString().split(":");
            ZonedDateTime evalDate = date.with(LocalTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1])));
            return date.isBefore(evalDate);
        }
    }

    private static class TimeAfterEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime date = left.asDateNode().getDate();
            String[] time = right.asStringNode().getString().split(":");
            ZonedDateTime evalDate = date.with(LocalTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1])));
            return date.isAfter(evalDate);
        }
    }

    private static class AllMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return evaluators.get(RelationalOperator.SUBSETOF).evaluate(right, left, ctx);
        }
    }

    private static class AnyMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return evaluators.get(RelationalOperator.ANYOF).evaluate(right, left, ctx);
        }
    }

    private static class NoneMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return evaluators.get(RelationalOperator.NONEOF).evaluate(right, left, ctx);
        }
    }

    private static class ExactMatchEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            return evaluators.get(RelationalOperator.ALL).evaluate(right, left, ctx);
        }
    }

    private static class WindowInEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime date = left.asDateNode().getDate();
            ZonedDateTime now = ZonedDateTime.now(date.getZone());
            int window = right.asNumberNode().getNumber().intValue();
            LocalDate finalDate = now.toLocalDate().minusDays(window);
            return date.toLocalDate().isAfter(finalDate);
        }
    }

    private static class WindowOutEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime date = left.asDateNode().getDate();
            ZonedDateTime now = ZonedDateTime.now(date.getZone());
            int window = right.asNumberNode().getNumber().intValue();
            LocalDate finalDate = date.toLocalDate().minusDays(window);
            return finalDate.isAfter(now.toLocalDate());
        }
    }

    private static class WindowTimeInEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime date = left.asDateNode().getDate();
            ZonedDateTime now = ZonedDateTime.now(date.getZone());
            int window = right.asNumberNode().getNumber().intValue();
            ZonedDateTime finalDate = now.minusDays(window);
            return date.isAfter(finalDate);
        }
    }

    private static class WindowTimeOutEvaluator implements Evaluator {
        @Override
        public boolean evaluate(ValueNode left, ValueNode right, Predicate.PredicateContext ctx) {
            ZonedDateTime date = left.asDateNode().getDate();
            ZonedDateTime now = ZonedDateTime.now(date.getZone());
            int window = right.asNumberNode().getNumber().intValue();
            ZonedDateTime finalDate = date.minusDays(window);
            return finalDate.isAfter(now);
        }
    }
}
