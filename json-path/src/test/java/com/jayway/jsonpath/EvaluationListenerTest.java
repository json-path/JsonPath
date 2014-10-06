package com.jayway.jsonpath;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EvaluationListenerTest extends BaseTest {


    @Test
    public void an_evaluation_listener_can_abort_after_one_result_using_fluent_api() {
        EvaluationListener firstResultListener = new EvaluationListener() {
            @Override
            public EvaluationContinuation resultFound(FoundResult found) {
                return EvaluationContinuation.ABORT;
            }
        };
        List<String> title = JsonPath.parse(JSON_DOCUMENT).withListeners(firstResultListener).read("$..title", List.class);
        assertThat(title).containsExactly("Sayings of the Century");
    }

    @Test
    public void an_evaluation_listener_can_abort_after_one_result_using_configuration() {
        EvaluationListener firstResultListener = new EvaluationListener() {
            @Override
            public EvaluationContinuation resultFound(FoundResult found) {
                return EvaluationContinuation.ABORT;
            }
        };
        Configuration configuration = Configuration.builder().evaluationListener(firstResultListener).build();

        List<String> title = JsonPath.using(configuration).parse(JSON_DOCUMENT).read("$..title", List.class);
        assertThat(title).containsExactly("Sayings of the Century");
    }

    @Test
    public void an_evaluation_lister_can_continue() {

        final List<Integer> idxs = new ArrayList<Integer>();

        EvaluationListener firstResultListener = new EvaluationListener() {
            @Override
            public EvaluationContinuation resultFound(FoundResult found) {
                idxs.add(found.index());
                return EvaluationContinuation.CONTINUE;
            }
        };
        List<String> title = JsonPath.parse(JSON_DOCUMENT).withListeners(firstResultListener).read("$..title", List.class);
        assertThat(title).containsExactly("Sayings of the Century", "Sword of Honour", "Moby Dick", "The Lord of the Rings");
        assertThat(idxs).containsExactly(0, 1, 2, 3);
    }


    @Test
    public void evaluation_results_can_be_limited() {

        List<String> res = JsonPath.parse(JSON_DOCUMENT).limit(1).read("$..title", List.class);
        assertThat(res).containsExactly("Sayings of the Century");

        res = JsonPath.parse(JSON_DOCUMENT).limit(2).read("$..title", List.class);
        assertThat(res).containsExactly("Sayings of the Century", "Sword of Honour");
    }

    @Test
    public void multiple_evaluation_listeners_can_be_added() {

        final List<Integer> idxs1 = new ArrayList<Integer>();
        final List<Integer> idxs2 = new ArrayList<Integer>();

        EvaluationListener listener1 = new EvaluationListener() {
            @Override
            public EvaluationContinuation resultFound(FoundResult found) {
                idxs1.add(found.index());
                return EvaluationContinuation.CONTINUE;
            }
        };

        EvaluationListener listener2 = new EvaluationListener() {
            @Override
            public EvaluationContinuation resultFound(FoundResult found) {
                idxs2.add(found.index());
                return EvaluationContinuation.CONTINUE;
            }
        };

        List<String> title = JsonPath.parse(JSON_DOCUMENT).withListeners(listener1, listener2).read("$..title", List.class);
        assertThat(title).containsExactly("Sayings of the Century", "Sword of Honour", "Moby Dick", "The Lord of the Rings");
        assertThat(idxs1).containsExactly(0, 1, 2, 3);
        assertThat(idxs2).containsExactly(0, 1, 2, 3);
    }

    @Test
    public void evaluation_listeners_can_be_cleared() {

        EvaluationListener listener = new EvaluationListener() {
            @Override
            public EvaluationContinuation resultFound(FoundResult found) {
                return EvaluationContinuation.CONTINUE;
            }
        };

        Configuration configuration1 = Configuration.builder().evaluationListener(listener).build();
        Configuration configuration2 = configuration1.setEvaluationListeners();

        assertThat(configuration1.getEvaluationListeners()).hasSize(1);
        assertThat(configuration2.getEvaluationListeners()).hasSize(0);
    }
}
