package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.JsonLocation.AbstractJsonLocation;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.internal.filter.PatternFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * a path token which matches property names by regex
 */
public class PatternPathToken extends PathToken {

    private static final Logger logger = LoggerFactory.getLogger(PatternPathToken.class);

    private final String pattern;
    private final Pattern compiledPattern;
    private final String flags;

    PatternPathToken(CharSequence charSequence) {
        compiledPattern = Utils.compilePattern(charSequence);
        pattern = compiledPattern.pattern();
        flags = PatternFlag.parseFlags(compiledPattern.flags());
    }


    @Override
    public void evaluate(AbstractJsonLocation currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        if (ctx.jsonProvider().isMap(model)) {
            List<String> props = new ArrayList<String>();
            for (String property : ctx.jsonProvider().getPropertyKeys(model)) {
                if (compiledPattern.matcher(property).matches()) {
                    logger.trace("Matched property {} against {} in path {}", property, compiledPattern, currentPath);
                    props.add(property);
                }
            }
            if(props.isEmpty()){
                logger.trace("No property matched agains {} in path {}",compiledPattern,currentPath);
                return;
            }
            handleObjectProperty(currentPath, model, ctx, props);
        }

    }

    @Override
    public boolean isTokenDefinite() {
        return false;
    }

    @Override
    protected String getPathFragment() {
        return "[/" + pattern + "/" + flags + "]";
    }
}
