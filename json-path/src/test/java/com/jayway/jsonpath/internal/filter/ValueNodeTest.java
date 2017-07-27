package com.jayway.jsonpath.internal.filter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configuration.Defaults;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValueNodeTest {

   private static class TestDefaults implements Defaults {

      TestDefaults() {}

      @Override
      public JsonProvider jsonProvider() {
         return new JacksonJsonNodeJsonProvider();
      }

      @Override
      public MappingProvider mappingProvider() {
         return new JacksonMappingProvider();
      }

      @Override
      public Set<Option> options() {
         return EnumSet.noneOf(Option.class);
      }
   }

   private static final String JSON = "{\n" +
         "    \"nodes\": {\n" +
         "        \"unnamed1\": {\n" +
         "            \"ntpServers\": [\n" +
         "                \"1.2.3.4\"\n" +
         "            ]\n" +
         "        }\n" +
         "    }\n" +
         "}";

   @Test
   public void testOneNtpServer() throws Exception {
      Configuration.setDefaults(new TestDefaults());
      DocumentContext ctx = JsonPath.using(Configuration.defaultConfiguration()).parse(JSON);

      String path = "$.nodes[*][?(!([\"1.2.3.4\"] subsetof @.ntpServers))].ntpServers";
      JsonPath jsonPath = JsonPath.compile(path);

      ctx.read(jsonPath);
   }
}