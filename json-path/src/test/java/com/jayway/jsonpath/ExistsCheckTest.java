package com.jayway.jsonpath;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExistsCheckTest extends BaseTest {

    @Test
    public void deep_products_exists_returns_only_store_b() {
        String json = "[" +
                "{\"id\":1,\"name\":\"Store A\",\"departments\":[{\"id\":10,\"name\":\"Electronics\",\"products\":[{\"id\":101,\"name\":\"TV\",\"in_stock\":true},{\"id\":102,\"name\":\"Laptop\",\"in_stock\":false}]},{\"id\":11,\"name\":\"Clothing\",\"products\":[{\"id\":201,\"name\":\"Shirt\",\"in_stock\":true},{\"id\":202,\"name\":\"Jeans\",\"in_stock\":true}]}]} ," +
                "{\"id\":2,\"name\":\"Store B\",\"departments\":[{\"id\":12,\"name\":\"Grocery\",\"products\":[{\"id\":301,\"name\":\"Milk\",\"in_stock\":false},{\"id\":302,\"name\":\"Eggs\",\"in_stock\":true}]}]}" +
                "]";

        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(json);

        List<String> res1 = JsonPath.read(doc, "$[?(@..products[?(@.id==302)])].name");
        assertThat(res1).containsExactly("Store B");

        List<String> res2 = JsonPath.read(doc, "$[?(@.departments[?(@.products[?(@.id==302)])])].name");
        assertThat(res2).containsExactly("Store B");
    }
}
