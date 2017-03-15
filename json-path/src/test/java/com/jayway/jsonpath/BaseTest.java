package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.PredicateContextImpl;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JettisonProvider;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.json.TapestryJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import com.jayway.jsonpath.spi.mapper.TapestryMappingProvider;

import java.util.HashMap;

public class BaseTest {

    public static final Configuration JSON_ORG_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonOrgMappingProvider())
            .jsonProvider(new JsonOrgJsonProvider())
            .build();

    public static final Configuration GSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new GsonMappingProvider())
            .jsonProvider(new GsonJsonProvider())
            .build();

    public static final Configuration JACKSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonProvider())
            .build();

    public static final Configuration JACKSON_JSON_NODE_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build();

    public static final Configuration JETTISON_CONFIGURATION = Configuration
            .builder()
            .jsonProvider(new JettisonProvider())
            .build();

    public static final Configuration JSON_SMART_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new JsonSmartMappingProvider())
            .jsonProvider(new JsonSmartJsonProvider())
            .build();

    public static final Configuration TAPESTRY_JSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new TapestryMappingProvider())
            .jsonProvider(TapestryJsonProvider.INSTANCE)
            .build();

    public static final String JSON_BOOK_DOCUMENT =
            "{ " +
                    "   \"category\" : \"reference\",\n" +
                    "   \"author\" : \"Nigel Rees\",\n" +
                    "   \"title\" : \"Sayings of the Century\",\n" +
                    "   \"display-price\" : 8.95\n" +
                    "}";
    public static final String JSON_DOCUMENT = "{\n" +
            "   \"string-property\" : \"string-value\", \n" +
            "   \"int-max-property\" : " + Integer.MAX_VALUE + ", \n" +
            "   \"long-max-property\" : " + Long.MAX_VALUE + ", \n" +
            "   \"boolean-property\" : true, \n" +
            "   \"null-property\" : null, \n" +
            "   \"int-small-property\" : 1, \n" +
            "   \"max-price\" : 10, \n" +
            "   \"store\" : {\n" +
            "      \"book\" : [\n" +
            "         {\n" +
            "            \"category\" : \"reference\",\n" +
            "            \"author\" : \"Nigel Rees\",\n" +
            "            \"title\" : \"Sayings of the Century\",\n" +
            "            \"display-price\" : 8.95\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"Evelyn Waugh\",\n" +
            "            \"title\" : \"Sword of Honour\",\n" +
            "            \"display-price\" : 12.99\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"Herman Melville\",\n" +
            "            \"title\" : \"Moby Dick\",\n" +
            "            \"isbn\" : \"0-553-21311-3\",\n" +
            "            \"display-price\" : 8.99\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"J. R. R. Tolkien\",\n" +
            "            \"title\" : \"The Lord of the Rings\",\n" +
            "            \"isbn\" : \"0-395-19395-8\",\n" +
            "            \"display-price\" : 22.99\n" +
            "         }\n" +
            "      ],\n" +
            "      \"bicycle\" : {\n" +
            "         \"foo\" : \"baz\",\n" +
            "         \"escape\" : \"Esc\\b\\f\\n\\r\\t\\n\\t\\u002A\",\n" +
            "         \"color\" : \"red\",\n" +
            "         \"display-price\" : 19.95,\n" +
            "         \"foo:bar\" : \"fooBar\",\n" +
            "         \"dot.notation\" : \"new\",\n" +
            "         \"dash-notation\" : \"dashes\"\n" +
            "      }\n" +
            "   },\n" +
            "   \"foo\" : \"bar\",\n" +
            "   \"@id\" : \"ID\"\n" +
            "}";

    public static final String JSON_AWS_LAMBDA_CONTEXT = "{\n" +
            "  \"body\": \"{\\\"test\\\":\\\"body\\\"}\",\n" +
            "  \"resource\": \"/{proxy+}\",\n" +
            "  \"requestContext\": {\n" +
            "    \"resourceId\": \"123456\",\n" +
            "    \"apiId\": \"1234567890\",\n" +
            "    \"resourcePath\": \"/{proxy+}\",\n" +
            "    \"httpMethod\": \"POST\",\n" +
            "    \"requestId\": \"c6af9ac6-7b61-11e6-9a41-93e8deadbeef\",\n" +
            "    \"accountId\": \"123456789012\",\n" +
            "    \"identity\": {\n" +
            "      \"apiKey\": null,\n" +
            "      \"userArn\": null,\n" +
            "      \"cognitoAuthenticationType\": null,\n" +
            "      \"caller\": null,\n" +
            "      \"userAgent\": \"Custom User Agent String\",\n" +
            "      \"user\": null,\n" +
            "      \"cognitoIdentityPoolId\": null,\n" +
            "      \"cognitoIdentityId\": null,\n" +
            "      \"cognitoAuthenticationProvider\": null,\n" +
            "      \"sourceIp\": \"127.0.0.1\",\n" +
            "      \"accountId\": null\n" +
            "    },\n" +
            "    \"stage\": \"prod\"\n" +
            "  },\n" +
            "  \"queryStringParameters\": {\n" +
            "    \"lat\": 50.0,\n" +
            "    \"lon\": 60.0,\n" +
            "    \"timeZone\": 1\n" +
            "  },\n" +
            "  \"headers\": {\n" +
            "    \"Via\": \"1.1 08f323deadbeefa7af34d5feb414ce27.cloudfront.net (CloudFront)\",\n" +
            "    \"Accept-Language\": \"en-US,en;q=0.8\",\n" +
            "    \"CloudFront-Is-Desktop-Viewer\": \"true\",\n" +
            "    \"CloudFront-Is-SmartTV-Viewer\": \"false\",\n" +
            "    \"CloudFront-Is-Mobile-Viewer\": \"false\",\n" +
            "    \"X-Forwarded-For\": \"127.0.0.1, 127.0.0.2\",\n" +
            "    \"CloudFront-Viewer-Country\": \"US\",\n" +
            "    \"Accept\": \"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\",\n" +
            "    \"Upgrade-Insecure-Requests\": \"1\",\n" +
            "    \"X-Forwarded-Port\": \"443\",\n" +
            "    \"Host\": \"1234567890.execute-api.us-east-1.amazonaws.com\",\n" +
            "    \"X-Forwarded-Proto\": \"https\",\n" +
            "    \"X-Amz-Cf-Id\": \"cDehVQoZnx43VYQb9j2-nvCh-9z396Uhbp027Y2JvkCPNLmGJHqlaA==\",\n" +
            "    \"CloudFront-Is-Tablet-Viewer\": \"false\",\n" +
            "    \"Cache-Control\": \"max-age=0\",\n" +
            "    \"User-Agent\": \"Custom User Agent String\",\n" +
            "    \"CloudFront-Forwarded-Proto\": \"https\",\n" +
            "    \"Accept-Encoding\": \"gzip, deflate, sdch\"\n" +
            "  },\n" +
            "  \"pathParameters\": {\n" +
            "    \"proxy\": \"path/to/resource\"\n" +
            "  },\n" +
            "  \"httpMethod\": \"POST\",\n" +
            "  \"stageVariables\": {\n" +
            "    \"baz\": \"qux\"\n" +
            "  },\n" +
            "  \"path\": \"/path/to/resource\"\n" +
            "}";


    public Predicate.PredicateContext createPredicateContext(final Object check) {

        return new PredicateContextImpl(check, check, Configuration.defaultConfiguration(), new HashMap<Path, Object>());
    }
}
