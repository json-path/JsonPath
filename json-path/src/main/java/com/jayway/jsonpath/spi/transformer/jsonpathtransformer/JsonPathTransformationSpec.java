package com.jayway.jsonpath.spi.transformer.jsonpathtransformer;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.transformer.TransformationSpec;
import com.jayway.jsonpath.spi.transformer.TransformationSpecValidationException;
import com.jayway.jsonpath.spi.transformer.ValidationError;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.JsonPathTransformerValidationError;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.LookupTable;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.PathMapping;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.TransformationModel;
import static com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.JsonPathTransformerValidationError.*;

import java.util.*;

/**
 * The Transformation Spec is essentially a JSON document with the following structure:
 * {
 *     "lookupTables" : [
 *        {
 *            "tableName" : "stateMapper",
 *            "tableData" : {
 *                "EN_ROUTE" : "ACTIVE",
 *                "PLANNED" : "DISPATCHED",
 *                "COMPLETED" : "COMPLETED"
 *            }
 *        },
 *        {
 *            "tableName" : "distMapper",
 *            "tableData" : {
 *               "DISTANCE" : "dist"
 *            }
 *        }
 *
 *     ],
 *     "pathMappings" : [
 *         {
 *         "source" : "$.shipment.id",
 *         "target" : "$.shipment.extid"
 *         }, {
 *         "source" : "$.shipment.state",
 *         "target" : "$.shipment.state",
 *         "lookupTable" : "stateMapper"
 *         }
 *     ]
 * }
 *
 * As the name indicates, this transformation provider uses JsonPath's to express the transformation
 * of the source document to the target document.
 *
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
 *
 * TODO: identify more Validations.
 */
public class JsonPathTransformationSpec  implements TransformationSpec {
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
                JsonPath compiledSrc = JsonPath.compile(mapping.getSource());
                JsonPath compiledTgt = JsonPath.compile(mapping.getTarget());
                boolean isSrcArrayWildCard = false;
                try {
                    isSrcArrayWildCard = isArrayWildCard(src);
                } catch(UnsupportedWildcardPathException ex) {
                    response.add(new JsonPathTransformerValidationError(UNSUPPORTED_WILDCARD_PATH, src));
                }
                boolean isTgtArrayWildCard = false;
                try {
                    isTgtArrayWildCard = isArrayWildCard(tgt);
                } catch(UnsupportedWildcardPathException ex) {
                    response.add(new JsonPathTransformerValidationError(UNSUPPORTED_WILDCARD_PATH, tgt));
                }

                boolean bothNotSame = isSrcArrayWildCard ^ isTgtArrayWildCard;

                if (!compiledSrc.isDefinite() && !isSrcArrayWildCard) {
                    response.add(new JsonPathTransformerValidationError(
                            PATH_NEITHER_DEFINITE_NOR_WILDCARD_ARRAY, src));

                }
                if (!compiledTgt.isDefinite() && !isTgtArrayWildCard) {
                    response.add(new JsonPathTransformerValidationError(
                            PATH_NEITHER_DEFINITE_NOR_WILDCARD_ARRAY, tgt));

                }
                if (bothNotSame) {
                    response.add(new JsonPathTransformerValidationError(
                            INVALID_WILDCARD_ARRAY_MAPPING,
                            src, isSrcArrayWildCard, tgt, isTgtArrayWildCard));

                }

            } catch (InvalidPathException  e) {
                response.add(new JsonPathTransformerValidationError(
                        INVALID_JSON_PATH, src , e.getMessage()));
            }

            try {
                JsonPath.compile(mapping.getTarget());
            } catch (InvalidPathException  e) {
                response.add(new JsonPathTransformerValidationError(
                        INVALID_JSON_PATH, tgt , e.getMessage()));
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
            if (m.getLookupTable() != null)  {
                boolean referenceFound = find(m.getLookupTable(), tables);
                if (!referenceFound) {
                    response.add(new JsonPathTransformerValidationError(
                            INVALID_LOOKUP_TABLE_REF, m.getLookupTable()));

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

    /* package */ static boolean isArrayWildCard(String path) {
        //TODO: We support only a single wild-card to begin with.
        path = path.replaceAll("\\s", "");
        ;
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

    /*package*/ static boolean isScalar(Object srcValue) {
        if (srcValue == null) {
            return true;
        }
        if (WRAPPER_TYPES.contains(srcValue.getClass())) {
            return true;
        }
        return false;
    }


}
