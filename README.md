### FunctionProvider SPI (Custom Function Plugins)

JsonPath allows you to define and register custom functions (plugins) for use in path expressions and filter predicates.

#### Example: `JoinFunction`

JsonPath includes [`com.jayway.jsonpath.spi.function.JoinFunction`](file:///Users/rpond/IdeaProjects/JsonPath/json-path/src/main/java/com/jayway/jsonpath/spi/function/JoinFunction.java) as an example of a custom plugin that joins values or JSON-path expressions with a delimiter.

```java
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.ParamType;
import com.jayway.jsonpath.internal.function.Parameter;
import com.jayway.jsonpath.spi.function.PathFunction;

import java.util.ArrayList;
import java.util.List;

public class JoinFunction implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        String delimiter = "";
        List<Parameter> targetParams = new ArrayList<>();

        if (parameters == null || parameters.isEmpty()) {
            delimiter = "";
        } else if (parameters.size() == 1) {
            Object d = parameters.get(0).getValue();
            delimiter = d != null ? d.toString() : "";
        } else {
            Parameter first = parameters.get(0);
            Parameter last = parameters.get(parameters.size() - 1);
            if (first.getType() != ParamType.PATH && last.getType() == ParamType.PATH) {
                // Delimiter is first argument: join(delimiter, path1, path2, ...)
                delimiter = first.getValue() != null ? first.getValue().toString() : "";
                targetParams = parameters.subList(1, parameters.size());
            } else if (first.getType() == ParamType.PATH && last.getType() != ParamType.PATH) {
                // Delimiter is last argument: join(path1, path2, ..., delimiter)
                delimiter = last.getValue() != null ? last.getValue().toString() : "";
                targetParams = parameters.subList(0, parameters.size() - 1);
            } else {
                delimiter = first.getValue() != null ? first.getValue().toString() : "";
                targetParams = parameters.subList(1, parameters.size());
            }
        }

        List<String> items = new ArrayList<>();
        if (!targetParams.isEmpty()) {
            for (Parameter param : targetParams) {
                appendValue(param.getValue(), ctx, items);
            }
        } else if (model != null) {
            appendValue(model, ctx, items);
        }

        return String.join(delimiter, items);
    }
    // ... helper to traverse arrays and scalars
}
```

#### Registering Custom Functions

Functions can be registered either per-`Configuration` (recommended) or globally.

**Configuration-scoped registration (isolated):**

```java
// Register by Class
Configuration conf = Configuration.builder()
        .registerFunction("join", JoinFunction.class)
        .build();

// Join multiple paths with delimiter:
String fullName = JsonPath.using(conf).parse(json)
        .read("$.join(' ', $.user.firstName, $.user.lastName)"); // e.g. "John Doe"

// Join wildcard/array path with delimiter:
String fruits = JsonPath.using(conf).parse(json)
        .read("$.join(', ', $.fruits[*])"); // e.g. "apple, banana, cherry"

// Or invoke directly on an array:
String joined = JsonPath.using(conf).parse(json)
        .read("$.fruits.join('-')"); // e.g. "apple-banana-cherry"

// Custom functions can also be used inside filter predicates:
List<Integer> ids = JsonPath.using(conf).parse(json)
        .read("$.items[?(@.tags.join(',') == 'a,b')].id");
```

**Global registration:**

```java
import com.jayway.jsonpath.internal.function.PathFunctionFactory;

PathFunctionFactory.registerFunction("join", JoinFunction.class);
// or:
PathFunctionFactory.registerFunction("join", new JoinFunction());

// Available across all default evaluations
String result = JsonPath.read(json, "$.join(' - ', $.user.firstName, $.user.lastName)");
```

### Cache SPI