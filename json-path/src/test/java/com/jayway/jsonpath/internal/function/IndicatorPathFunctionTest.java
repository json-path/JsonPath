package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;

public class IndicatorPathFunctionTest extends BaseFunctionTest {
    protected static final String INDICATORS_SERIES = "{\"sma3\": [100.05, 100.05, 100.05, 100.2, 99.55, 100.25, 102.2, 102.9167], " +
                                                        "\"ema3\": [100.05, 100.025, 100.5625, 100.0313, 99.0406, 101.1203, 103.2352, 101.7176], " +
                                                        "\"prices\" : [100, 101.1, 99.05, 100, 101.1, 99.5, 98.05, 103.2, 105.35, 100.2], " +
                                                        "\"empty\": []}";


    public static Iterable<Configuration> configurations() {
        return Configurations.configurations();
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testSMA(Configuration conf) {
        List<Double> expectedResult = new ArrayList<>(Arrays.asList(100.05d, 100.05d, 100.05d, 100.2d, 99.55d, 100.25d, 102.2d, 102.9167d));
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.prices.sma(3,4)", ArrayList.class);
        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testSMAOfEmpty(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.empty.sma(3,4)", ArrayList.class);
        assertThat(result).isEqualTo(new ArrayList<>());
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testSMAIncorrectWindow(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.prices.sma(11,4)", ArrayList.class);
        assertThat(result).isEqualTo(new ArrayList<>());
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testSMASingleResult(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.prices.sma(10,3)", ArrayList.class);
        assertThat(result).isEqualTo(Collections.singletonList(100.755d));
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testEMA(Configuration conf) {
        List<Double> expectedResult = new ArrayList<>(Arrays.asList(100.05d, 100.025d, 100.5625d, 100.0313d, 99.0406d, 101.1203d, 103.2352d, 101.7176d));
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.prices.ema(3,4)", ArrayList.class);
        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testEMAOfEmpty(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.empty.ema(3,4)", ArrayList.class);
        assertThat(result).isEqualTo(new ArrayList<>());
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testEMAIncorrectWindow(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.prices.ema(11,4)", ArrayList.class);
        assertThat(result).isEqualTo(new ArrayList<>());
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testEMASingleResult(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.prices.sma(10,3)", ArrayList.class);
        assertThat(result).isEqualTo(Collections.singletonList(100.755d));
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testRSI(Configuration conf) {
        List<Double> expectedResult = new ArrayList<>(Arrays.asList(50.0d, 64.3478d, 39.5722d, 25.9763d, 73.8470d, 81.3853d, 39.9799d));
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.prices.rsi(3,4)", ArrayList.class);
        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testRSIOfEmpty(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.empty.rsi(3,4)", ArrayList.class);
        assertThat(result).isEqualTo(new ArrayList<>());
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testRSIIncorrectWindow(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.prices.rsi(10,4)", ArrayList.class);
        assertThat(result).isEqualTo(new ArrayList<>());
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testDIFF(Configuration conf) {
        List<Double> expectedResult = new ArrayList<>(Arrays.asList(0.0d, 0.025d, -0.5125d, 0.1687d, 0.5094d, -0.8703d, -1.0352d, 1.1991d));
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.sma3.diff($.ema3,4)", ArrayList.class);
        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("configurations")
    public void testDIFFOfEmpty(Configuration conf) {
        List<?> result = using(conf).parse(INDICATORS_SERIES).read("$.empty.diff($.empty,4)", ArrayList.class);
        assertThat(result).isEqualTo(new ArrayList<>());
    }
}
