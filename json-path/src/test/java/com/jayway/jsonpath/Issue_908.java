package com.jayway.jsonpath;

import org.junit.Test;

public class Issue_908 {

  @Test(expected = InvalidPathException.class)
  public void test_when_current_context_is_null() {
    JsonPath
        .parse("{\"test\" : null }")
        .read("$.test[?(@ != null)]");
  }

  @Test
  public void test_suppress_exception_when_current_context_is_null() {

    Object rs = JsonPath.using(Configuration.builder()
            .options(Option.SUPPRESS_EXCEPTIONS).build())
        .parse("{\"test\" : null }")
        .read("$.test[?(@ != null)]");
   
    assert(rs.toString().equals("[]"));
  }

}
