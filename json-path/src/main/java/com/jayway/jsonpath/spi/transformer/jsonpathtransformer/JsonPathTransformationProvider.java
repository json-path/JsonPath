package com.jayway.jsonpath.spi.transformer.jsonpathtransformer;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.transformer.*;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.LookupTable;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.PathMapping;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.SourceTransform;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.TransformationModel;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.jayway.jsonpath.spi.transformer.jsonpathtransformer.JsonPathTransformationSpec.isArrayWildCard;
import static com.jayway.jsonpath.spi.transformer.jsonpathtransformer.JsonPathTransformationSpec.isScalar;
import static com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.JsonPathTransformerValidationError.*;


public class JsonPathTransformationProvider implements TransformationProvider<JsonPathTransformationSpec> {


    private static Map<String, String> inferredTypes = new HashMap<>();

    private static enum InferredTypesCombo {
        II_I,
        IS_I,
        IL_L,
        LL_L,
        SS_S,
        SI_I,
        LI_L,
        LS_L,
        SL_L,
        IF_F,
        FI_F,
        FS_F,
        FL_F,
        SF_F,
        LF_F,
        FF_F,
        FD_D,
        DF_D,
        ID_D,
        DI_D,
        DS_D,
        DD_D,
        SD_D,
        LD_D,
        DL_D
    }

    static {
        for (InferredTypesCombo s: InferredTypesCombo.values()) {
            String[] str = s.name().split("_");
            //System.out.println(str[0] + ":" + str[1]);
            inferredTypes.put(str[0],str[1]);

        }

    }

    @Override
    public TransformationSpec spec(String input, Configuration configuration) {
        JsonPathTransformationSpec ret;

        if (input == null) {
            throw new IllegalArgumentException("parameter 'input' cannot be null");
        }
        try {
            TransformationModel model = configuration.mappingProvider().map(
                    configuration.jsonProvider().parse(input),
                    TransformationModel.class, configuration);
            ret = new JsonPathTransformationSpec(model);
        } catch (Exception ex) {
            throw new TransformationSpecValidationException(ex);
        }
        //Note: some implementations of the transformation provider might choose not to call validate implicitly
        //instead it will be a separate API call from the client.
        List<ValidationError> errors = ret.validate();
        if (errors != null && !errors.isEmpty()) {
            throw new TransformationSpecValidationException(stringifyErrors(errors));
        }
        return ret;
    }


    @Override
    public TransformationSpec spec(InputStream input, Configuration configuration) {

        JsonPathTransformationSpec ret;

        if (input == null) {
            throw new IllegalArgumentException(
                    getStringFromBundle(NULL_PARAMETER, "input"));
        }
        try {
            TransformationModel model = configuration.mappingProvider().map(
                    configuration.jsonProvider().parse(input, Charset.defaultCharset().name()),
                    TransformationModel.class, configuration);
            ret = new JsonPathTransformationSpec(model);
        } catch (Exception ex) {
            throw new TransformationSpecValidationException(ex);
        }
        //Note: some implementations of the transformation provider might choose not to call validate implicitly
        //instead it will be a separate API call from the client.
        List<ValidationError> errors = ret.validate();
        if (errors != null && !errors.isEmpty()) {
            throw new TransformationSpecValidationException(stringifyErrors(errors));
        }
        return ret;

    }

    @Override
    public Object transform(Object source, JsonPathTransformationSpec spec, Configuration configuration) {

        configuration.addOptions(Option.CREATE_MISSING_PROPERTIES_ON_DEFINITE_PATH);

        //start with an empty InputObject
        String inputObject = "{ }";
        Object transformed = null;
        boolean first = true;

        DocumentContext jsonContext = JsonPath.parse(source);
        TransformationModel model = (TransformationModel) spec.get();

        for (PathMapping pm : model.getPathMappings()) {

            String srcPath = pm.getSource();
            if (srcPath != null) {
                JsonPath compiledSrcPath = JsonPath.compile(srcPath);
                JsonPath compiledDstPath = JsonPath.compile(pm.getTarget());

                if (!compiledSrcPath.isDefinite() && !compiledDstPath.isDefinite()
                        && isArrayWildCard(pm.getSource()) && isArrayWildCard(pm.getTarget())) {
                    //TODO: handle multiple wild-cards : construct a tree and each path from root to leaf
                    //would then provide one expanded Path.
                    List<PathMapping> expanded = computedExpandedPathForArrays(
                            source, srcPath, pm.getTarget(), configuration);
                    for (PathMapping exp : expanded) {
                        if (first) {
                            transformed = transform(
                                    exp, first, inputObject, configuration, jsonContext, transformed, model
                                    , pm.getLookupTable());
                            first = false;
                        } else {
                            transformed = transform(
                                    exp, first, inputObject, configuration, jsonContext, transformed, model
                                    , pm.getLookupTable());
                        }

                    }
                    continue;

                }
            }

            if (first) {
                transformed = transform(
                        pm, first, inputObject, configuration, jsonContext, transformed, model
                        , pm.getLookupTable());
                first = false;
            } else {
                transformed = transform(
                        pm, first, inputObject, configuration, jsonContext, transformed, model
                        , pm.getLookupTable());
            }
        }

        return transformed;
    }

    private Object transform(PathMapping pm,
                             boolean first, String inputObject,
                             Configuration configuration, DocumentContext jsonContext, Object transformed,
                             TransformationModel model, String lookupTable) {


        String srcPath = pm.getSource();
        JsonPath compiledSrcPath = null;

        if (srcPath != null) {
            compiledSrcPath  = JsonPath.compile(srcPath);
        }

        JsonPath compiledDstPath = JsonPath.compile(pm.getTarget());
        Object srcValue = null;
        boolean srcValueIsConstant = false;

        if (srcPath == null && pm.getAdditionalTransform() == null && pm.getTarget() != null) {
           //throw No sourcepath value specified for setting target path
            throw new TransformationException(
                    getStringFromBundle(NO_SOURCE_VALUE_FOR_MAPPING, pm.getTarget()));
        }
        if (pm.getTarget() == null) {
            //throw target cannot be null
            throw new TransformationException(
                    getStringFromBundle(MISSING_TARGET_PATH_FOR_MAPPING,
                            (srcPath != null) ? srcPath : "null"));
        }
        if (pm.getAdditionalTransform() != null) {
            if (pm.getAdditionalTransform().getConstantSourceValue() != null &&
                    pm.getAdditionalTransform().getSourcePath() != null) {
                //throw ambiguous additionalTransform Operand
                throw new TransformationException(
                        getStringFromBundle(AMBIGUOUS_ADDITIONAL_TRANSFORM,
                                srcPath));
            }
            if (srcPath == null && pm.getAdditionalTransform().getConstantSourceValue() != null) {
                if (pm.getAdditionalTransform().getOperator() != null) {
                    //throw ambiguous operator since sourcePath is null
                    throw new TransformationException(
                            getStringFromBundle(INVALID_OPERATOR_NULL_SRC_PATH,
                                    pm.getAdditionalTransform().getOperator()));
                }
                //allows a constant value to be set directly onto the target path
                srcValue = pm.getAdditionalTransform().getConstantSourceValue();
                srcValueIsConstant = true;

            }
            if (pm.getAdditionalTransform().getSourcePath() != null) {
                if (pm.getAdditionalTransform().getOperator() == null) {
                    // throw missing operator for additional source transform
                    throw new TransformationException(
                            getStringFromBundle(MISSING_OPERATOR,
                                    srcPath, pm.getAdditionalTransform().getSourcePath()));
                }
            }
        }
        if (!srcValueIsConstant && srcPath != null) {
            try {
                srcValue = jsonContext.read(compiledSrcPath);
            } catch (PathNotFoundException ex) {
                // if the source path does not exist then nothing to do
                // just return what came in
                // TODO: log here. we are going to ignore any additionalTransform as well.
                return transformed;
            }
        }

        //assert srcValue is a scalar type. We do not want an Array
        if (!isScalar(srcValue)) {
            //now check if its an array of size 1
            //applicable when the src path has filter predicates
            if (configuration.jsonProvider().isArray(srcValue) &&
                    configuration.jsonProvider().length(srcValue) == 1) {
                srcValue = configuration.jsonProvider().getArrayIndex(srcValue, 0);
            } else {
                throw new TransformationException(
                        getStringFromBundle(SOURCE_NOT_SCALAR, srcPath, srcValue.getClass().getName()));
            }
        }
        if (lookupTable != null && (srcValue instanceof String)) {
            srcValue = lookup(srcValue, lookupTable, model.getLookupTables());
        }

        //process any additional source transforms
        Object additonalSourceValue = null;
        if (!srcValueIsConstant && pm.getAdditionalTransform() != null) {
            if (pm.getAdditionalTransform().getConstantSourceValue() != null) {
                //case of a constant operand with operator to be applied on srcValue
                additonalSourceValue = pm.getAdditionalTransform().getConstantSourceValue();
            } else if (pm.getAdditionalTransform().getSourcePath() != null) {
                //case of additional JsonPath from source document with operator to be applied
                //on srcValue
                String additionalSrcPath =
                        pm.getAdditionalTransform().getSourcePath();
                try {
                    additonalSourceValue =
                            jsonContext.read(JsonPath.compile(additionalSrcPath));
                } catch (PathNotFoundException ex) {
                    throw new TransformationException(ex);
                }

            }
            if (additonalSourceValue != null) {
                checkDataTypesAndOperator(
                        srcValue, additonalSourceValue,
                        SourceTransform.AllowedOperation.valueOf(
                                pm.getAdditionalTransform().getOperator()));

                srcValue = applyAddtionalTransform(
                        srcValue, additonalSourceValue, SourceTransform.AllowedOperation.valueOf(
                                pm.getAdditionalTransform().getOperator()));
            }
        }
        if (first) {
            transformed = compiledDstPath.set(configuration.jsonProvider().parse(inputObject),
                    srcValue, configuration);
        } else {
            transformed = compiledDstPath.set(transformed, srcValue, configuration);
        }

        return transformed;
    }

    private void checkDataTypesAndOperator(
            Object srcValue, Object additonalSourceValue,
                  SourceTransform.AllowedOperation operator) {

        if (srcValue != null && additonalSourceValue != null && operator == null) {
            //throw Operator null while source operands non-null
            throw new TransformationException(
                    getStringFromBundle(MISSING_OPERATOR,
                            srcValue, additonalSourceValue));

        }
        if (operator != null && operator.getType().startsWith("UNARY")) {
            if (srcValue != null && additonalSourceValue != null) {
                //throw invalid Unary Operator NOT with Two Operands
                throw new TransformationException(
                        getStringFromBundle(INVALID_UNARY_OPERATOR,
                                srcValue, additonalSourceValue));
            }
        } else if (operator != null && anyOperandIsNull(srcValue, additonalSourceValue)) {
            //throw invalid Binary operator, one of the operands is null
            throw new TransformationException(
                    getStringFromBundle(INVALID_BINARY_OPERATOR, operator));
        }

        if (srcValue instanceof Number && !SourceTransform.NUMERIC.equals(operator.getType())) {
            //throw expected numeric operator but found
            throw new TransformationException(
                    getStringFromBundle(INVALID_OPERATOR_FOR_TYPE,
                            operator.name(), "numeric", operator.getType()));
        } else if (srcValue instanceof String && !SourceTransform.STRING.equals(operator.getType())) {
            //throw expected String operator but found
            throw new TransformationException(
                    getStringFromBundle(INVALID_OPERATOR_FOR_TYPE,
                            operator.name(), "string", operator.getType()));
        } else if (srcValue instanceof Boolean && !SourceTransform.BOOLEAN.equals(operator.getType())) {
            //throw expected boolean operator but found
            throw new TransformationException(
                    getStringFromBundle(INVALID_OPERATOR_FOR_TYPE,
                            operator.name(), "boolean", operator.getType()));
        }

        if (additonalSourceValue instanceof Number && !SourceTransform.NUMERIC.equals(operator.getType())) {
            //throw expected numeric operator but found
            throw new TransformationException(
                    getStringFromBundle(INVALID_OPERATOR_FOR_TYPE,
                            operator.name(), "numeric", operator.getType()));
        } else if (additonalSourceValue instanceof String && !SourceTransform.STRING.equals(operator.getType())) {
            //throw expected String operator but found
            throw new TransformationException(
                    getStringFromBundle(INVALID_OPERATOR_FOR_TYPE,
                            operator.name(), "string", operator.getType()));
        } else if (additonalSourceValue instanceof Boolean && !SourceTransform.BOOLEAN.equals(operator.getType())) {
            //throw expected boolean operator but found
            throw new TransformationException(
                    getStringFromBundle(INVALID_OPERATOR_FOR_TYPE,
                            operator.name(), "boolean", operator.getType()));
        }

    }

    private boolean anyOperandIsNull(Object srcValue, Object additonalSourceValue) {
        if (srcValue == null || additonalSourceValue == null) {
            return true;
        }
        return false;
    }

    private Object lookup(Object srcValue, String lookupTable, LookupTable[] lookupTables) {
        for (LookupTable l : lookupTables) {
            if (l.getTableName().equals(lookupTable)) {
                if (!l.getTableData().containsKey(srcValue)) {
                    // TODO: log a warning and return the srcValue unchanged
                    return srcValue;
                }
                return l.getTableData().get(srcValue);
            }
        }
        // if the lookup table is not found
        throw new TransformationException(
                getStringFromBundle(INVALID_LOOKUP_TABLE_REF, lookupTable));
    }

    private List<PathMapping> computedExpandedPathForArrays(
            Object source, String srcPath, String dstPath, Configuration configuration) {

        //We support only a single wild-card to begin with.
        String srcpathTrimmed = srcPath.replaceAll("\\s", "");
        int firstIndex = srcpathTrimmed.indexOf("[*]");
        if (firstIndex == -1) {
            throw new TransformationException("c");
        }
        String pathTillArray = srcpathTrimmed.substring(0, firstIndex + 3);
        //query the source document to figure out how many entries exist in the  source array
        List<Object> items = JsonPath.read(source, pathTillArray);
        if (items == null) {
            throw new TransformationException(
                    getStringFromBundle(NULL_QUERY_RESULT, pathTillArray));

        }
        int size = items.size();
        String dstpathTrimmed = dstPath.replaceAll("\\s", "");
        firstIndex = dstpathTrimmed.indexOf("[*]");
        if (firstIndex == -1) {
            throw new TransformationException(getStringFromBundle(INTERNAL_ERROR));
        }

        List<PathMapping> result = new ArrayList<PathMapping>();

        for (int i = 0; i < size; i++) {
            PathMapping p = new PathMapping();
            p.setSource(srcpathTrimmed.replace("[*]", "[" + i + "]"));
            p.setTarget(dstpathTrimmed.replace("[*]", "[" + i + "]"));
            result.add(p);
        }

        return result;
    }


    private String stringifyErrors(List<ValidationError> errors) {

        if (errors == null) {
            throw new IllegalArgumentException(getStringFromBundle(NULL_PARAMETER,"errors"));
        }

        StringBuilder builder = new StringBuilder("\n{\n");
        for (int i = 0; i < errors.size(); i++) {
            builder = (i == (errors.size() - 1)) ? builder.append(errors.get(i)) :
                    builder.append(errors.get(i));
        }
        builder.append("}\n");
        return builder.toString();
    }

    private BiFunction<Number, Number, Number> add = (a, b) -> addNumbers(a, b, getInferredType(a,b));
    private BiFunction<Number, Number, Number> sub = (a, b) -> subNumbers(a, b, getInferredType(a,b));
    private BiFunction<Number, Number, Number> mul = (a, b) -> mulNumbers(a, b, getInferredType(a,b));
    private BiFunction<Number, Number, Number> div = (a, b) -> divNumbers(a, b, getInferredType(a,b));
    private BiFunction<String, String, String> concat = (a, b) -> a+b;
    private BiFunction<Boolean, Boolean, Boolean> and = (a, b) -> a && b;
    private BiFunction<Boolean, Boolean, Boolean> or = (a, b) -> a || b;
    private Function<Boolean, Boolean> not = (a) ->  !a ;
    private BiFunction<Boolean, Boolean, Boolean> xor = (a, b) -> a ^ b;

    private static String getInferredType(Number a, Number b) {
        String ab = (a.getClass().getName().substring(10,11).toUpperCase())
                + (b.getClass().getName().substring(10,11).toUpperCase());
        //System.out.println(ab);
        return inferredTypes.get(ab);
        //System.out.println(inf);
        //return inf;
    }


    private static Number addNumbers(Number a, Number b, String inferredType) {

        switch(inferredType) {
            case "I" :
                return a.intValue() + b.intValue();
            case "L" :
                return a.longValue() + b.longValue();
            case "F" :
                return a.floatValue() + b.floatValue();
            case "D" :
                return a.doubleValue() + b.doubleValue();
            case "S" :
            default:
                //same as double
                return a.doubleValue() + b.doubleValue();
        }
    }

    private static Number subNumbers(Number a, Number b, String inferredType) {

        switch(inferredType) {
            case "I" :
                return a.intValue() - b.intValue();
            case "L" :
                return a.longValue() - b.longValue();
            case "F" :
                return a.floatValue() - b.floatValue();
            case "D" :
                return a.doubleValue() - b.doubleValue();
            case "S" :
            default:
                //same as double
                return a.doubleValue() - b.doubleValue();
        }
    }

    private static Number mulNumbers(Number a, Number b, String inferredType) {

        switch(inferredType) {
            case "I" :
                return a.intValue() * b.intValue();
            case "L" :
                return a.longValue() * b.longValue();
            case "F" :
                return a.floatValue() * b.floatValue();
            case "D" :
                return a.doubleValue() * b.doubleValue();
            case "S" :
            default:
                //same as double
                return a.doubleValue() * b.doubleValue();
        }
    }

    private static Number divNumbers(Number a, Number b, String inferredType) {

        switch(inferredType) {
            case "I" :
                return a.intValue() / b.intValue();
            case "L" :
                return a.longValue() / b.longValue();
            case "F" :
                return a.floatValue() / b.floatValue();
            case "D" :
                return a.doubleValue() / b.doubleValue();
            case "S" :
            default:
                //same as double
                return a.doubleValue() / b.doubleValue();
        }
    }

    private Object applyAddtionalTransform(
            Object srcValue, Object srcPostProcessingVal, SourceTransform.AllowedOperation op) {
        switch (op) {
            case ADD:
                return add.apply((Number) srcValue, (Number) srcPostProcessingVal);
            case LHS_SUB:
               return sub.apply((Number) srcPostProcessingVal, (Number) srcValue);
            case RHS_SUB:
                return sub.apply((Number) srcValue, (Number) srcPostProcessingVal);
            case MUL:
                return mul.apply((Number) srcValue, (Number) srcPostProcessingVal);
            case LHS_DIV:
                return div.apply((Number) srcPostProcessingVal, (Number) srcValue);
            case RHS_DIV:
                return div.apply((Number) srcValue, (Number) srcPostProcessingVal);
            case LHS_STRING_CONCAT:
                return concat.apply((String) srcPostProcessingVal, (String) srcValue);
            case RHS_STRING_CONCAT:
                return concat.apply((String) srcValue, (String) srcPostProcessingVal);
            case OR:
                return or.apply((Boolean)srcValue, (Boolean)srcPostProcessingVal);
            case XOR:
                return xor.apply((Boolean)srcValue, (Boolean)srcPostProcessingVal);
            case AND:
                return and.apply((Boolean)srcValue, (Boolean)srcPostProcessingVal);
            case NOT:
                return not.apply((Boolean)srcValue);

            default:
                throw new TransformationException(
                        getStringFromBundle(UNSUPPORTED_OPERATION, op.name()));
        }
    }

}
