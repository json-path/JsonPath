package com.jayway.jsonpath.internal.path.eval;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.EvaluatorDispatcher;
import com.jayway.jsonpath.internal.path.token.ArrayPathToken;
import com.jayway.jsonpath.internal.path.token.PathToken;
import com.jayway.jsonpath.internal.path.token.PropertyPathToken;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.List;

import static java.lang.String.format;

public abstract class TokenEvaluator<P extends PathToken> implements ITokenEvaluator<P> {

  protected final EvaluatorDispatcher dispatcher;

  public TokenEvaluator(final EvaluatorDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  public abstract void evaluate(final P token, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx);

  /**
   * Check if model is non-null and array.
   * @param currentPath
   * @param model
   * @param ctx
   * @return false if current evaluation call must be skipped, true otherwise
   * @throws PathNotFoundException if model is null and evaluation must be interrupted
   * @throws InvalidPathException if model is not an array and evaluation must be interrupted
   */
  public boolean checkArrayModel(final ArrayPathToken token, String currentPath, Object model, EvaluationContextImpl ctx) {
    if (model == null){
      if (! token.isUpstreamDefinite()) {
        return false;
      } else {
        throw new PathNotFoundException("The path " + currentPath + " is null");
      }
    }
    if (!ctx.jsonProvider().isArray(model)) {
      if (! token.isUpstreamDefinite()) {
        return false;
      } else {
        throw new PathNotFoundException(format("Filter: %s can only be applied to arrays. Current context is: %s", toString(), model));
      }
    }
    return true;
  }


  public void handleObjectProperty(final P token, String currentPath, Object model, EvaluationContextImpl ctx, List<String> properties) {

    if(properties.size() == 1) {
      String property = properties.get(0);
      String evalPath = Utils.concat(currentPath, "['", property, "']");
      Object propertyVal = readObjectProperty(property, model, ctx);
      if(propertyVal == JsonProvider.UNDEFINED){
        // Conditions below heavily depend on current token type (and its logic) and are not "universal",
        // so this code is quite dangerous (I'd rather rewrite it & move to PropertyPathToken and implemented
        // WildcardPathToken as a dynamic multi prop case of PropertyPathToken).
        // Better safe than sorry.
        assert token instanceof PropertyPathToken : "only PropertyPathToken is supported";

        if(token.isLeaf()) {
          if(ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)){
            propertyVal =  null;
          } else {
            if(ctx.options().contains(Option.SUPPRESS_EXCEPTIONS) ||
              !ctx.options().contains(Option.REQUIRE_PROPERTIES)){
              return;
            } else {
              throw new PathNotFoundException("No results for path: " + evalPath);
            }
          }
        } else {
          if (! (token.isUpstreamDefinite() && token.isTokenDefinite()) &&
            !ctx.options().contains(Option.REQUIRE_PROPERTIES) ||
            ctx.options().contains(Option.SUPPRESS_EXCEPTIONS)){
            // If there is some indefiniteness in the path and properties are not required - we'll ignore
            // absent property. And also in case of exception suppression - so that other path evaluation
            // branches could be examined.
            return;
          } else {
            throw new PathNotFoundException("Missing property in path " + evalPath);
          }
        }
      }
      PathRef pathRef = ctx.forUpdate() ? PathRef.create(model, property) : PathRef.NO_OP;
      if (token.isLeaf()) {
        ctx.addResult(evalPath, pathRef, propertyVal);
      }
      else {
        dispatcher.evaluate(token.next(), evalPath, pathRef, propertyVal, ctx);
      }
    } else {
      String evalPath = currentPath + "[" + Utils.join(", ", "'", properties) + "]";

      assert token.isLeaf() : "non-leaf multi props handled elsewhere";

      Object merged = ctx.jsonProvider().createMap();
      for (String property : properties) {
        Object propertyVal;
        if(hasProperty(property, model, ctx)) {
          propertyVal = readObjectProperty(property, model, ctx);
          if(propertyVal == JsonProvider.UNDEFINED){
            if(ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)) {
              propertyVal = null;
            } else {
              continue;
            }
          }
        } else {
          if(ctx.options().contains(Option.DEFAULT_PATH_LEAF_TO_NULL)){
            propertyVal = null;
          } else if (ctx.options().contains(Option.REQUIRE_PROPERTIES)) {
            throw new PathNotFoundException("Missing property in path " + evalPath);
          } else {
            continue;
          }
        }
        ctx.jsonProvider().setProperty(merged, property, propertyVal);
      }
      PathRef pathRef = ctx.forUpdate() ? PathRef.create(model, properties) : PathRef.NO_OP;
      ctx.addResult(evalPath, pathRef, merged);
    }
  }

  private static boolean hasProperty(String property, Object model, EvaluationContextImpl ctx) {
    return ctx.jsonProvider().getPropertyKeys(model).contains(property);
  }

  private static Object readObjectProperty(String property, Object model, EvaluationContextImpl ctx) {
    return ctx.jsonProvider().getMapValue(model, property);
  }


  public void handleArrayIndex(final P token, int index, String currentPath, Object model, EvaluationContextImpl ctx) {
    String evalPath = Utils.concat(currentPath, "[", String.valueOf(index), "]");
    PathRef pathRef = ctx.forUpdate() ? PathRef.create(model, index) : PathRef.NO_OP;
    try {
      Object evalHit = ctx.jsonProvider().getArrayIndex(model, index);
      if (token.isLeaf()) {
        ctx.addResult(evalPath, pathRef, evalHit);
      } else {
        dispatcher.evaluate(token.next(), evalPath, pathRef, evalHit, ctx);
      }
    } catch (IndexOutOfBoundsException e) {
    }
  }
}
