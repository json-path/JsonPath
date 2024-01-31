package com.jayway.jsonpath.internal.function.text;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;

import java.util.List;


/**
 * Getting the substring of a JSONArray Object or a String Object.
 * This function will dump all inputs into string. Thus, it is flexible.
 * There is a sign for books like "E" for "Military books" on the head or tail.
 * User can use this function to work on it.
 *
 * Examples:
 *    EG1:
 *       JSON string: "{'RfRaw':{'Data':'ABC436F601F405783CE00E55'}}"
 *       JSON-Path command: $.RfRaw.Data.substring(16,22)
 *
 *       The resul of "$.RfRaw.Data" is "ABC436F601F405783CE00E55"
 *       Then get the substring "3CE00E"
 *   EG2:
 *       JSON string: "{'RfRaw':{'Data':'ABC436F601F405783CE00E55'}}"
 *       JSON-Path command: $.RfRaw.substring(1,5)
 *
 *       The resul of "$.RfRaw.Data" is "{Data=ABC436F601F405783CE00E55}"
 *       Then get the substring "Data"
 *
 *
 * Created by XiaoLing12138 on 04/24/2022.
 */
public class Substring implements PathFunction {

    public static final String TOKEN_NAME = "substring";

    /**
     * We can use this function to get a substring from the json file. It can be the key value which is a string or a
     * JSON-Path Node.
     *
     * @param currentPath
     *      The current path location inclusive of the function name
     *
     * @param parent
     *      The path location above the current function
     *
     * @param model
     *      The JSON model as input to this particular function.
     *      You take it as the JSON object we need to deal with.
     *
     * @param ctx
     *      Eval context, state bag used as the path is traversed, maintains the result of executing
     *
     * @param parameters
     *      The input parameters. Here is the index of the substring
     *
     *
     * @return A substring
     */
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        String result = model.toString();
        int indexHead = Integer.parseInt(parameters.get(0).getJson());
        int indexTail = Integer.parseInt(parameters.get(1).getJson());

        if (indexHead >= indexTail) {
            return "";
        }

        return result.substring(indexHead, indexTail);
    }
}
