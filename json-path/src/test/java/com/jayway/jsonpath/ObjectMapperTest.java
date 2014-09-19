package com.jayway.jsonpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.mapper.Mapper;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapperTest {


    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void mapping() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", "MAP FOO");
        map.put("bar", Long.MAX_VALUE);

        Baz baz = new Baz();
        baz.setFlurb("FLURB");

        FooBar fooBar = new FooBar();
        fooBar.setFoo("OBJ FOO");
        fooBar.setBar(Long.MIN_VALUE);
        fooBar.setBaz(baz);


        Map mappedMap = objectMapper.convertValue(fooBar, Map.class);


        System.out.println(mappedMap);
        /*
        FooBar foobar = objectMapper.convertValue(map, FooBar.class);


        Integer integer = objectMapper.convertValue(1L, Integer.class);
        Integer integer2 = objectMapper.convertValue(1D, Integer.class);
        String date = objectMapper.convertValue(new Date(System.currentTimeMillis()), String.class);
        */



        Mapper mapper = new Mapper();


        Long convert = mapper.getMapper(Long.class).convert(1D);

        System.out.println(convert);

    }


    public static class FooBar {
        private String foo;
        private Long bar;

        private Baz baz;

        public FooBar() {
        }

        public Baz getBaz() {
            return baz;
        }

        public void setBaz(Baz baz) {
            this.baz = baz;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public Long getBar() {
            return bar;
        }

        public void setBar(Long bar) {
            this.bar = bar;
        }
    }

    public static class Baz {
        private String flurb;

        public Baz() {
        }

        public String getFlurb() {
            return flurb;
        }

        public void setFlurb(String flurb) {
            this.flurb = flurb;
        }
    }


}
