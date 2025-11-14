package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.function.ParamType;
import com.jayway.jsonpath.internal.function.Parameter;

import java.util.Arrays;

public class ScannerFunctionInverter {

    private final RootPathToken path;

    public ScannerFunctionInverter(RootPathToken path) {
        this.path = path;
    }

    public RootPathToken invert() {
        if (path.isFunctionPath() && path.next() instanceof ScanPathToken) {
            PathToken token = path;
            PathToken prior = null;
            while (null != (token = token.next()) && !(token instanceof FunctionPathToken)) {
                prior = token;
            }
            if (token instanceof FunctionPathToken) {
                prior.setNext(null);
                path.setTail(prior);

                Parameter parameter = new Parameter();
                parameter.setPath(new CompiledPath(path, true));
                parameter.setType(ParamType.PATH);
                ((FunctionPathToken)token).setParameters(Arrays.asList(parameter));
                RootPathToken functionRoot = new RootPathToken('$');
                functionRoot.setTail(token);
                functionRoot.setNext(token);

                return functionRoot;
            }
        }
        return path;
    }
}
