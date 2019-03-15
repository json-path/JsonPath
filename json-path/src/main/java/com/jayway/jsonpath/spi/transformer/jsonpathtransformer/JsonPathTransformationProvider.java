package com.jayway.jsonpath.spi.transformer.jsonpathtransformer;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.transformer.*;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.LookupTable;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.PathMapping;
import com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.TransformationModel;
import static com.jayway.jsonpath.spi.transformer.jsonpathtransformer.JsonPathTransformationSpec.*;
import static com.jayway.jsonpath.spi.transformer.jsonpathtransformer.model.JsonPathTransformerValidationError.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;


public class JsonPathTransformationProvider implements TransformationProvider<JsonPathTransformationSpec> {


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
        JsonPath compiledSrcPath = JsonPath.compile(srcPath);
        JsonPath compiledDstPath = JsonPath.compile(pm.getTarget());

        Object srcValue = jsonContext.read(compiledSrcPath);
        //assert srcValue is a scalar type. We do not want an Array
        if (!isScalar(srcValue)) {
            throw new TransformationException(
                    getStringFromBundle(SOURCE_NOT_SCALAR, srcValue.getClass().getName()));
        }
        if (lookupTable != null) {
            srcValue = lookup(srcValue, lookupTable, model.getLookupTables());
        }
        if (first) {
            transformed = compiledDstPath.set(configuration.jsonProvider().parse(inputObject),
                    srcValue, configuration);
        } else {
            transformed = compiledDstPath.set(transformed, srcValue, configuration);
        }

        return transformed;
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

}
