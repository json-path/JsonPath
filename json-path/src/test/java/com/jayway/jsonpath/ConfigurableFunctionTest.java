package com.jayway.jsonpath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.internal.function.PathFunction;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import java.util.List;
import org.junit.Test;

public class ConfigurableFunctionTest {
  private static String CONSTANT = "A constant value";
  public static class MyFunction implements PathFunction {

    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx,
        List<Parameter> parameters) {
      return CONSTANT;
    }
  }

  @Test
  public void testConfigurableCustomFunction() {
    Configuration withoutFunction = Configuration
        .builder()
        .mappingProvider(new JsonOrgMappingProvider())
        .jsonProvider(new JsonOrgJsonProvider())
        .build();

    Configuration withFunction = Configuration
        .builder()
        .mappingProvider(new JsonOrgMappingProvider())
        .addFunction("constant", MyFunction.class)
        .jsonProvider(new JsonOrgJsonProvider())
        .build();
    assertThat(JsonPath.using(withFunction).parse("{}")
        .read("$.constant()")).isEqualTo(CONSTANT);
    try {
      JsonPath.using(withoutFunction).parse("{}")
          .read("$.constant()");
      failBecauseExceptionWasNotThrown(InvalidPathException.class);
    } catch (InvalidPathException e) {
      assertThat(e.getMessage()).isEqualTo("Function with name: constant does not exist.");
    }
  }
}
