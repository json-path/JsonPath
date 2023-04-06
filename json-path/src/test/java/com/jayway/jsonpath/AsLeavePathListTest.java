package com.jayway.jsonpath;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.junit.Assert.assertArrayEquals;

/**
 * This is the test for the As_Leave_Path_List option
 * Idea from Issue#53 user MarcinNowak-codes
 *
 * The AS_LEAVE_PATH_LIST will return only leave path when use [*]
 * Otherwise it is same as AS_PATH_LIST
 *
 * Issue link: https://github.com/json-path/JsonPath/issues/753
 * Created by XiaoLing12138 on 05/21/2022.
 */
public class AsLeavePathListTest {
    /**
     * Here define two kinds of option for later testing
     */
    public static final Configuration configurationLeave = Configuration.builder().options(Option.AS_LEAVE_PATH_LIST).build();
    public static final Configuration configurationOrigin = Configuration.builder().options(Option.AS_PATH_LIST).build();

    /**
     * Here define two kinds of json path for later testing
     */
    public static final String jsonLeave = "{ " +
            "\"name\": \"Martin\", " +
            "\"family\": \"Newman\", " +
            "\"phones\": [\"+48 612 34 56 78\", \"+49 12 34 56 78 90\"], " +
            "\"age\": 18, " +
            "\"married\": true, " +
            "\"salary\": {" +
                "\"amount\": 12.10, " +
                "\"currency\":\"EURO\"" +
            "}, " +
            "\"kids\": [{" +
                "\"name\": \"Lena\"" +
            "}, {" +
                "\"name\": \"Luna\"" +
            "}, {" +
                "\"name\": \"Lego\"" +
            "}]}";

    public static final String jsonStore = "{ " +
            "\"store\": { " +
            "\"book\": [ { " +
                "\"category\": \"reference\", " +
                "\"author\": \"Nigel Rees\", " +
                "\"title\": \"Sayings of the Century\", " +
                "\"price\": 8.95 " +
            "}, { " +
                "\"category\": \"fiction\", " +
                "\"author\": \"Evelyn Waugh\", " +
                "\"title\": \"Sword of Honour\", " +
                "\"price\": 12.99 " +
            "}, { " +
                "\"category\": \"fiction\", " +
                "\"author\": \"Herman Melville\", " +
                "\"title\": \"Moby Dick\", " +
                "\"isbn\": \"0-553-21311-3\", " +
                "\"price\": 8.99 " +
            "}, { " +
                "\"category\": \"fiction\", " +
                "\"author\": \"J. R. R. Tolkien\", " +
                "\"title\": \"The Lord of the Rings\", " +
                "\"isbn\": \"0-395-19395-8\", " +
                "\"price\": 22.99 " +
            "} ], " +
            "\"bicycle\": { " +
                "\"color\": \"red\", " +
                "\"price\": 19.95 " +
            "} }, " +
            "\"expensive\": 10 " +
            "}";

    /**
     * This test for original AS_PATH_LIST option to JSONStore.
     * Aiming to confirm the stability of the system.
     * Test for path $..[*]
     */
    @Test
    public void TestOriginPathOne() {
        List<String> pathList = using(configurationOrigin).parse(jsonStore).read("$..[*]");
        String[] strings = {
                "$['store']",
                "$['expensive']",
                "$['store']['book']",
                "$['store']['bicycle']",
                "$['store']['book'][0]",
                "$['store']['book'][1]",
                "$['store']['book'][2]",
                "$['store']['book'][3]",
                "$['store']['book'][0]['category']",
                "$['store']['book'][0]['author']",
                "$['store']['book'][0]['title']",
                "$['store']['book'][0]['price']",
                "$['store']['book'][1]['category']",
                "$['store']['book'][1]['author']",
                "$['store']['book'][1]['title']",
                "$['store']['book'][1]['price']",
                "$['store']['book'][2]['category']",
                "$['store']['book'][2]['author']",
                "$['store']['book'][2]['title']",
                "$['store']['book'][2]['isbn']",
                "$['store']['book'][2]['price']",
                "$['store']['book'][3]['category']",
                "$['store']['book'][3]['author']",
                "$['store']['book'][3]['title']",
                "$['store']['book'][3]['isbn']",
                "$['store']['book'][3]['price']",
                "$['store']['bicycle']['color']",
                "$['store']['bicycle']['price']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_PATH_LIST option to JSONStore.
     * Aiming to confirm the stability of the system.
     * Test for path $..['author']
     */
    @Test
    public void TestOriginPathTwo() {
        List<String> pathList = using(configurationOrigin).parse(jsonStore).read("$..['author']");
        String[] strings = {
                "$['store']['book'][0]['author']",
                "$['store']['book'][1]['author']",
                "$['store']['book'][2]['author']",
                "$['store']['book'][3]['author']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_PATH_LIST option to JSONStore.
     * Aiming to confirm the stability of the system.
     * Test for path $..['book']
     */
    @Test
    public void TestOriginPathThree() {
        List<String> pathList = using(configurationOrigin).parse(jsonStore).read("$..['book']");
        String[] strings = {"$['store']['book']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_PATH_LIST option to JSONLeave.
     * Aiming to confirm the stability of the system.
     * Test for path $..[*]
     */
    @Test
    public void TestOriginPathFour() {
        List<String> pathList = using(configurationOrigin).parse(jsonLeave).read("$..[*]");
        String[] strings = {
                "$['name']",
                "$['family']",
                "$['phones']",
                "$['age']",
                "$['married']",
                "$['salary']",
                "$['kids']",
                "$['phones'][0]",
                "$['phones'][1]",
                "$['salary']['amount']",
                "$['salary']['currency']",
                "$['kids'][0]",
                "$['kids'][1]",
                "$['kids'][2]",
                "$['kids'][0]['name']",
                "$['kids'][1]['name']",
                "$['kids'][2]['name']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_PATH_LIST option to JSONLeave.
     * Aiming to confirm the stability of the system.
     * Test for path $..['name']
     */
    @Test
    public void TestOriginPathFive() {
        List<String> pathList = using(configurationOrigin).parse(jsonLeave).read("$..['name']");
        String[] strings = {
                "$['name']",
                "$['kids'][0]['name']",
                "$['kids'][1]['name']",
                "$['kids'][2]['name']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_PATH_LIST option to JSONLeave.
     * Aiming to confirm the stability of the system.
     * Test for path $..['kids']
     */
    @Test
    public void TestOriginPathSix() {
        List<String> pathList = using(configurationOrigin).parse(jsonLeave).read("$..['kids']");
        String[] strings = {"$['kids']"};
        for (String s : pathList) {
            System.out.println(s);
        }
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for AS_LEAVE_PATH_LIST option to JSONStore.
     * Aiming to confirm the stability of the system.
     * Test for path $..[*]
     */
    @Test
    public void TestLeavePathOne() {
        List<String> pathList = using(configurationLeave).parse(jsonStore).read("$..[*]");
        String[] strings = {
                "$['expensive']",
                "$['store']['book'][0]['category']",
                "$['store']['book'][0]['author']",
                "$['store']['book'][0]['title']",
                "$['store']['book'][0]['price']",
                "$['store']['book'][1]['category']",
                "$['store']['book'][1]['author']",
                "$['store']['book'][1]['title']",
                "$['store']['book'][1]['price']",
                "$['store']['book'][2]['category']",
                "$['store']['book'][2]['author']",
                "$['store']['book'][2]['title']",
                "$['store']['book'][2]['isbn']",
                "$['store']['book'][2]['price']",
                "$['store']['book'][3]['category']",
                "$['store']['book'][3]['author']",
                "$['store']['book'][3]['title']",
                "$['store']['book'][3]['isbn']",
                "$['store']['book'][3]['price']",
                "$['store']['bicycle']['color']",
                "$['store']['bicycle']['price']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_LEAVE_PATH_LIST option to JSONStore
     * Aiming to confirm the stability of the system.
     * Test for path $..['author']
     */
    @Test
    public void TestLeavePathTwo() {
        List<String> pathList = using(configurationLeave).parse(jsonStore).read("$..['author']");
        String[] strings = {
                "$['store']['book'][0]['author']",
                "$['store']['book'][1]['author']",
                "$['store']['book'][2]['author']",
                "$['store']['book'][3]['author']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_LEAVE_PATH_LIST option to JSONStore
     * Aiming to confirm the stability of the system.
     * Test for path $..['book']
     */
    @Test
    public void TestLeavePathThree() {
        List<String> pathList = using(configurationLeave).parse(jsonStore).read("$..['book']");
        String[] strings = {"$['store']['book']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_LEAVE_PATH_LIST option to JSONLeave
     * Aiming to test the function.
     * Test for path $..[*]
     */
    @Test
    public void TestLeavePathFour() {
        List<String> pathList = using(configurationLeave).parse(jsonLeave).read("$..[*]");
        String[] strings = {
                "$['name']",
                "$['family']",
                "$['age']",
                "$['married']",
                "$['phones'][0]",
                "$['phones'][1]",
                "$['salary']['amount']",
                "$['salary']['currency']",
                "$['kids'][0]['name']",
                "$['kids'][1]['name']",
                "$['kids'][2]['name']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_LEAVE_PATH_LIST option to JSONLeave
     * Aiming to test the function.
     * Test for path $..['name']
     */
    @Test
    public void TestLeavePathFive() {
        List<String> pathList = using(configurationLeave).parse(jsonLeave).read("$..['name']");
        String[] strings = {
                "$['name']",
                "$['kids'][0]['name']",
                "$['kids'][1]['name']",
                "$['kids'][2]['name']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

    /**
     * This test for original AS_LEAVE_PATH_LIST option to JSONLeave
     * Aiming to test the function.
     * Test for path $..['kids']
     */
    @Test
    public void TestLeavePathSix() {
        List<String> pathList = using(configurationLeave).parse(jsonLeave).read("$..['kids']");
        String[] strings = {"$['kids']"};
        assertArrayEquals(pathList.toArray(), Arrays.stream(strings).toArray());
    }

}
