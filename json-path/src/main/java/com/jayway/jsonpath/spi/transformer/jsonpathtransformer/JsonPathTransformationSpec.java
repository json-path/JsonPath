package com.jayway.jsonpath.spi.transformer.jsonpathtransformer;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.spi.transformer.TransformationSpec;
import com.jayway.jsonpath.spi.transformer.TransformationSpecValidationException;
import com.jayway.jsonpath.spi.transformer.ValidationError;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.*;

import static com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.JsonPathTransformerValidationError.*;

import java.util.*;

/**
 * The Transformation Spec is essentially a JSON document with the following structure:
 * {
 * "lookupTables" : [
 * {
 * "tableName" : "stateMapper",
 * "tableData" : {
 * "EN_ROUTE" : "ACTIVE",
 * "PLANNED" : "DISPATCHED",
 * "COMPLETED" : "COMPLETED"
 * }
 * },
 * {
 * "tableName" : "distMapper",
 * "tableData" : {
 * "DISTANCE" : "dist"
 * }
 * }
 * <p>
 * ],
 * "pathMappings" : [
 * {
 * "source" : "$.shipment.id",
 * "target" : "$.shipment.extid"
 * }, {
 * "source" : "$.shipment.state",
 * "target" : "$.shipment.state",
 * "lookupTable" : "stateMapper"
 * }
 * ]
 * }
 * <p>
 * As the name indicates, this transformation provider uses JsonPath's to express the transformation
 * of the source document to the target document.
 * <p>
 * 1. Target's should be definite singular paths suitable for writing.
 * 2. Source should be definite singular and can contain all that is supported by JsonPath (including filters/Functions)
 * 3. Array Source/Target can additionally specify a "*" to indicate same mapping for all elements.
 * The spec expects this to be the predominant way of mapping arrays. So when the source has a "*" then
 * ensure the destination also has a "*".
 * Eventually the spec will support multiple wildcards in the path, but the first
 * version will support only a single wildcard for array references in the path.
 * 4. the "lookupTable" if specified must match an existing tableName under lookupTables.
 * 5. Lookuptables are strictly string-to-string mappings and are primarily intended to capture enumerations
 * which can differ in the source and target domains.
 * 6. Need to support slices of arrays. Will be handled in future
 * <p>
 * TODO: identify more Validations.
 */
public class JsonPathTransformationSpec implements TransformationSpec {

    public static final String UNARY = "unary";
    /*
     * The Object could be whatever is returned by the underlying JsonProvider used to
     * parse the SPEC.
     */
    TransformationModel spec;

    JsonPathTransformationSpec(TransformationModel spec) {
        this.spec = spec;
    }

    @Override
    public Object get() {
        return spec;
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        errors.addAll(validateDefinitePathsAndArrayWildCardRules());
        errors.addAll(validateLookupTableRules());
        errors.addAll(validateAddtionalTransforms());
        return errors;
    }


    private Collection<? extends ValidationError> validateDefinitePathsAndArrayWildCardRules() {
        List<ValidationError> response = new ArrayList<ValidationError>();

        PathMapping[] mappings = spec.getPathMappings();
        for (PathMapping mapping : mappings) {
            //first validate the JsonPath syntax
            String src = mapping.getSource();
            String tgt = mapping.getTarget();
            try {
                //with the introduction of addtionalTransform, src can be null
                if (src == null && mapping.getAdditionalTransform() == null) {
                    //TODO: throw invalid mapping both source and additional transform cannot be null
                    response.add(new JsonPathTransformerValidationError(NULL_SOURCE));
                    continue;
                }
                if (tgt == null) {
                    response.add(new JsonPathTransformerValidationError(NULL_TARGET));
                    continue;
                }

                JsonPath compiledSrc = null;
                boolean isSrcArrayWildCard = false;
                if (src != null) {
                    compiledSrc = JsonPath.compile(mapping.getSource());
                    try {
                        isSrcArrayWildCard = isArrayWildCard(src);
                    } catch (UnsupportedWildcardPathException ex) {
                        response.add(new JsonPathTransformerValidationError(UNSUPPORTED_WILDCARD_PATH, src));
                    }
                }
                JsonPath compiledTgt = JsonPath.compile(mapping.getTarget());
                boolean isTgtArrayWildCard = false;
                try {
                    isTgtArrayWildCard = isArrayWildCard(tgt);
                } catch (UnsupportedWildcardPathException ex) {
                    response.add(new JsonPathTransformerValidationError(UNSUPPORTED_WILDCARD_PATH, tgt));
                }

                boolean bothNotSame = isSrcArrayWildCard ^ isTgtArrayWildCard;


                //On the source side we would want to allow predicate expressions, if they
                //do not evaluate to a scalar value at runtime then the isScalar check would
                //fail.
                // example: $.shipment.stops[?(@.name == 'source')].location'
                //response.add(new JsonPathTransformerValidationError(
                //      PATH_NEITHER_DEFINITE_NOR_WILDCARD_ARRAY, src));
                /*if (!compiledSrc.isFunctionPath() && !compiledSrc.isDefinite() && !isSrcArrayWildCard) {
                    response.add(new JsonPathTransformerValidationError(
                            PATH_NEITHER_DEFINITE_NOR_WILDCARD_ARRAY, src));

                }*/

                if (src != null && !compiledSrc.isFunctionPath() && !compiledTgt.isDefinite() && !isTgtArrayWildCard) {
                    response.add(new JsonPathTransformerValidationError(
                            PATH_NEITHER_DEFINITE_NOR_WILDCARD_ARRAY, tgt));

                }
                if (bothNotSame) {
                    response.add(new JsonPathTransformerValidationError(
                            INVALID_WILDCARD_ARRAY_MAPPING,
                            (src != null) ? src : "null", isSrcArrayWildCard, tgt, isTgtArrayWildCard));

                }

            } catch (InvalidPathException e) {
                response.add(new JsonPathTransformerValidationError(
                        INVALID_JSON_PATH, (src != null) ? src : "null", e.getMessage()));
            }

            try {
                JsonPath.compile(mapping.getTarget());
            } catch (InvalidPathException e) {
                response.add(new JsonPathTransformerValidationError(
                        INVALID_JSON_PATH, tgt, e.getMessage()));
            }
        }
        return response;
    }

    private Collection<? extends ValidationError> validateLookupTableRules() {
        //the Rule which says lookuptables should be Map: String -> String
        //is already implicitly handled in by the MappingProvider.
        List<ValidationError> response = new ArrayList<ValidationError>();
        PathMapping[] mappings = spec.getPathMappings();
        LookupTable[] tables = spec.getLookupTables();
        if ((tables == null) || tables.length == 0) {
            return response;
        }
        for (LookupTable t : tables) {
            //make sure all LookupTables have a name
            if (t.getTableName() == null || t.getTableName().isEmpty()) {
                response.add(new JsonPathTransformerValidationError(MISSING_TABLE_NAME));
            }
            //make sure all lookuptables have non-empty tableData.
            if (t.getTableData() == null || t.getTableData().isEmpty()) {
                response.add(new JsonPathTransformerValidationError(
                        MISSING_TABLE_DATA,
                        (t.getTableName() == null) ? "null" : t.getTableName()));
            }
        }

        for (PathMapping m : mappings) {
            if (m.getLookupTable() != null) {
                boolean referenceFound = find(m.getLookupTable(), tables);
                if (!referenceFound) {
                    response.add(new JsonPathTransformerValidationError(
                            INVALID_LOOKUP_TABLE_REF, m.getLookupTable()));

                }
            }
        }
        return response;
    }


    //TODO: need to restructure it a bit.
    private Collection<? extends ValidationError> validateAddtionalTransforms() {
        List<ValidationError> response = new ArrayList<ValidationError>();

        PathMapping[] mappings = spec.getPathMappings();
        for (PathMapping mapping : mappings) {

            SourceTransform additionalTransform = mapping.getAdditionalTransform();
            if (additionalTransform == null) {
                continue;
            }

            //first validate the JsonPath syntax
            String src = mapping.getSource();
            String tgt = mapping.getTarget();

            String additionalSrc = additionalTransform.getSourcePath();
            Object constantSrcValue = additionalTransform.getConstantSourceValue();
            String operator = additionalTransform.getOperator();

            JsonPath compiledSrcPath = null;
            if (additionalSrc != null) {
                try {
                    compiledSrcPath = JsonPath.compile(additionalSrc);
                } catch (InvalidPathException ex) {
                    response.add(new JsonPathTransformerValidationError(
                            INVALID_JSON_PATH, additionalSrc, ex.getMessage()));
                }
            }

            SourceTransform.AllowedOperation operatorEnum = null;
            if (operator != null) {
                try {
                    operatorEnum = SourceTransform.AllowedOperation.valueOf(operator);
                } catch (IllegalArgumentException ex) {
                    response.add(new JsonPathTransformerValidationError(
                            INVALID_OPERATOR, operator));
                }
            }
            // if constantValue specified then its a valid wrapper type and a scalar
            if (constantSrcValue != null) {
                if (!isScalar(constantSrcValue)) {
                    response.add(new JsonPathTransformerValidationError(
                            INVALID_CONSTANT_NOT_SCALAR));
                }

            }
            if (src == null && operator != null) {

                //throw invalid operator with null source path
                response.add(new JsonPathTransformerValidationError(
                        INVALID_OPERATOR_WITH_SRC_NULL, operator));

            }
            if (src != null && operator != null) {
                if (operatorEnum.getType().startsWith(UNARY)) {
                    if (additionalSrc != null
                            || constantSrcValue != null) {
                        //Invalid Unary operator for binary operands
                        response.add(new JsonPathTransformerValidationError(
                                INVALID_UNARY_OPERATOR, src, additionalSrc));
                    }
                }
            } else if (src != null && operator == null) {
                if (additionalSrc != null
                        || constantSrcValue != null) {
                    //missing operator
                    response.add(new JsonPathTransformerValidationError(
                            MISSING_OPERATOR, src,
                            additionalSrc != null ?
                                    additionalSrc :
                                    constantSrcValue));
                }

            }

            //ensure one of them is present
            if (additionalSrc != null
                    && constantSrcValue != null) {
                //Invalid additionalTransform, expected only one of sourcePath or constantSource
                response.add(new JsonPathTransformerValidationError(
                        INVALID_ADDITIONAL_TRANSFORM,
                        constantSrcValue,additionalSrc));

            } else if (additionalSrc == null &&
                    constantSrcValue == null && operator != null &&
                    !operatorEnum.getType().startsWith(UNARY)) {
                //throw invalid additional transform, one of sourcePath or constantSource should be non-null
                response.add(new JsonPathTransformerValidationError(
                        NULL_ADDITIONAL_TRANSFORM));

                if (src != null) {
                    response.add(new JsonPathTransformerValidationError(
                            MISSING_OPERAND_FOR_BINARY_OPERATOR, operator));
                }
            }
        }

        return response;
    }


    private boolean find(String lookupTable, LookupTable[] tables) {
        if ((tables == null) || tables.length == 0) {
            return false;
        }
        for (LookupTable t : tables) {
            if (lookupTable.equals(t.getTableName())) {
                return true;
            }
        }
        return false;
    }

    /* package */
    static boolean isArrayWildCard(String path) {
        //TODO: We support only a single wild-card to begin with.
        path = path.replaceAll("\\s", "");
        JsonPath compiled = JsonPath.compile(path);
        if (compiled.isFunctionPath()) {
            return false;
        }
        int lastIndex = path.lastIndexOf("[*]");
        int firstIndex = path.indexOf("[*]");
        if (firstIndex != -1) {
            if (lastIndex == firstIndex) {
                return true;
            } else {
                //this is an array wildcard however its currently unsupported
                throw new UnsupportedWildcardPathException(
                        getStringFromBundle(UNSUPPORTED_WILDCARD_PATH, path));

            }
        }
        //its not an array wildcard case
        return false;
    }

    private static final Set<Class> WRAPPER_TYPES = new HashSet<Class>(
            Arrays.asList(Integer.class, Boolean.class, Float.class, Double.class, String.class,
                    Character.class, Byte.class, Short.class, Long.class));

    /*package*/
    static boolean isScalar(Object srcValue) {
        if (srcValue == null) {
            return true;
        }
        if (WRAPPER_TYPES.contains(srcValue.getClass())) {
            return true;
        }

        return false;
    }


}
