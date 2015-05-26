package com.jayway.jsonpath;

import org.junit.Test;

import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonFactory;

import com.jayway.jsonpath.EvaluationCallback;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathCompiler;
import com.jayway.jsonpath.internal.token.*;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTest extends BaseTest implements EvaluationCallback {

    @Test
    public void an_object_can_be_mapped_to_pojo() {

        String json = "{\n" +
                "   \"foo\" : \"foo\",\n" +
                "   \"bar\" : 10,\n" +
                "   \"baz\" : true\n" +
                "}";


        FooBarBaz fooBarBaz = JsonPath.using(JACKSON_CONFIGURATION).parse(json).read("$", FooBarBaz.class);

        assertThat(fooBarBaz.foo).isEqualTo("foo");
        assertThat(fooBarBaz.bar).isEqualTo(10L);
        assertThat(fooBarBaz.baz).isEqualTo(true);

    }

    public static class FooBarBaz {
        public String foo;
        public Long bar;
        public boolean baz;
    }

    @Test
    public void jackson_converts_dates() {

        Date now = new Date();

        Object json = singletonMap("date_as_long", now.getTime());

        Date date = JsonPath.using(JACKSON_CONFIGURATION).parse(json).read("$['date_as_long']", Date.class);

        assertThat(date).isEqualTo(now);
    }

    protected TokenStack stack = null;
    protected CallbackRecorder recorder = null;
    protected Path idPath = null;
    protected int match = 0;

    @Test
    public void streamingTest() throws Exception {

        Path path =
            PathCompiler.compile("$..completed_tasks[0:]");
        idPath =
            PathCompiler.compile("$..completed_tasks[0:].id");
        stack = new TokenStack(JACKSON_CONFIGURATION);

        recorder = new CallbackRecorder();
        String res = "issue_76.json";
        InputStream stream =
            getClass().getClassLoader().getResourceAsStream(res);
        assert(stream != null);

        JsonFactory factory = new JsonFactory();
        stack.registerPath(path);
        stack.registerPath(idPath);
        stack.read(factory.createJsonParser(stream), this);
        Thread.sleep(1000);

        assert(recorder.getResults().get(0).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(1).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(2).
               equals(new CallbackRecorder.CallbackEvent(path, true)));
        assert(recorder.getResults().get(3).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(4).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(5).
               equals(new CallbackRecorder.CallbackEvent(path, true)));
        assert(recorder.getResults().get(6).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(7).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(8).
               equals(new CallbackRecorder.CallbackEvent(path, true)));
        assert(recorder.getResults().get(9).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(10).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(11).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(12).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(13).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(14).
               equals(new CallbackRecorder.CallbackEvent(path, true)));
        assert(recorder.getResults().get(15).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(16).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(17).
               equals(new CallbackRecorder.CallbackEvent(path, true)));
        assert(recorder.getResults().get(18).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(19).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(20).
               equals(new CallbackRecorder.CallbackEvent(path, true)));
        assert(recorder.getResults().get(21).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(22).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(23).
               equals(new CallbackRecorder.CallbackEvent(path, true)));


        assert(recorder.getResults().get(24).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(25).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(26).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(27).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(28).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(29).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(30).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(31).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(32).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(33).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(34).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(35).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(36).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(37).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(38).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(39).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(40).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(41).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(42).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(43).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(44).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(45).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(46).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(47).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(48).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(49).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(50).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(51).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(52).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(53).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(54).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(55).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(56).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(57).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(58).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(59).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(60).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(61).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(62).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(63).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(64).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(65).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(66).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(67).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(68).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(69).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(70).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(71).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(72).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(73).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(74).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(75).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(76).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(77).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(78).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(79).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(80).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(81).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(82).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(83).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(84).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(85).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(86).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(87).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(88).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(89).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(90).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(91).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(92).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(93).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(94).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(95).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(96).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(97).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(98).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(99).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(100).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(101).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(102).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(103).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(104).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(105).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(106).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(107).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(108).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(109).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(110).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(111).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(112).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(113).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(114).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(115).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(116).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(117).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(118).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(119).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(120).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(121).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(122).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(123).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(124).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(125).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(126).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(127).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(128).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(129).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(130).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(131).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(132).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(133).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(134).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(135).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(136).
               equals(new CallbackRecorder.CallbackEvent(path, true)));

        assert(recorder.getResults().get(137).
               equals(new CallbackRecorder.CallbackEvent(path, false)));
        assert(recorder.getResults().get(138).
               equals(new CallbackRecorder.CallbackEvent(idPath, false)));
        assert(recorder.getResults().get(139).
               equals(new CallbackRecorder.CallbackEvent(path, true)));
    }

    public void resultFound(Path path) {
        if (path == idPath) {
            switch (match++) {
            case 0:
                checkResult(stack, "mesos-jenkins-172c6f74-12bc-44fe-849a-ad902ddc2b64");
                break;
            case 1:
                checkResult(stack, "mesos-jenkins-cd18b433-e2ee-4a4e-875b-6ea82d90a53a");
                break;
            case 2:
                checkResult(stack, "mesos-jenkins-66fca7c0-88f7-4a1a-9796-a6de2e337b0a");
                break;
            case 3:
                checkResult(stack, "mesos-jenkins-258ad47c-85bf-4fe0-b7c4-4f0cc70f2998");
                break;
            case 4:
                checkResult(stack, "mesos-jenkins-f30e5e34-2ef6-4993-9260-73ee3520c0a5");
                break;
            case 5:
                checkResult(stack, "mesos-jenkins-1fc0bbd8-f0ff-48cd-a7f7-24a9436ba6bc");
                break;
            case 6:
                checkResult(stack, "mesos-jenkins-62b44026-843d-4813-a6a8-e95215250bb1");
                break;
            case 7:
                checkResult(stack, "mesos-jenkins-807b9b8a-3283-4e78-94e1-9dc17df4df77");
                break;
            case 8:
                checkResult(stack, "test_dv_helloworld.95f49d05-c9bf-11e4-a1fc-56847afe9799");
                break;
            case 9:
                checkResult(stack, "test_dv_helloworld.a40abda6-ca02-11e4-a1fc-56847afe9799");
                break;
            case 10:
                checkResult(stack, "test_dv_helloworld.3946a287-ca67-11e4-a1fc-56847afe9799");
                break;
            case 11:
                checkResult(stack, "test_dv_helloworld.fec0c74c-ce11-11e4-a1fc-56847afe9799");
                break;
            case 12:
                checkResult(stack, "test_jenkins-master.33d4007e-cf37-11e4-a1fc-56847afe9799");
                break;
            case 13:
                checkResult(stack, "test_jenkins-master.359deb0f-cf37-11e4-a1fc-56847afe9799");
                break;
            case 14:
                checkResult(stack, "test_jenkins-master.50773d6a-cf37-11e4-a1fc-56847afe9799");
                break;
            case 15:
                checkResult(stack, "test_jenkins-master.638fd9ce-cf37-11e4-a1fc-56847afe9799");
                break;
            case 16:
                checkResult(stack, "test_jenkins-master.81fd2852-cf37-11e4-a1fc-56847afe9799");
                break;
            case 17:
                checkResult(stack, "test_jenkins-master.afedb77b-cf37-11e4-a1fc-56847afe9799");
                break;
            case 18:
                checkResult(stack, "test_jenkins-master.0243c011-cf38-11e4-a1fc-56847afe9799");
                break;
            case 19:
                checkResult(stack, "test_dv_helloworld.4bdb1099-d1de-11e4-a1fc-56847afe9799");
                break;
            case 20:
                checkResult(stack, "sdp_shared_jenkins-master.eb8f2863-c9a2-11e4-a1fc-56847afe9799");
                break;
            case 21:
                checkResult(stack, "test_dv_helloworld.c7865efb-cd05-11e4-a1fc-56847afe9799");
                break;
            case 22:
                checkResult(stack, "test_jenkins-master.37684ad0-cf37-11e4-a1fc-56847afe9799");
                break;
            case 23:
                checkResult(stack, "test_jenkins-master.3c2e3752-cf37-11e4-a1fc-56847afe9799");
                break;
            case 24:
                checkResult(stack, "test_jenkins-master.405b3f34-cf37-11e4-a1fc-56847afe9799");
                break;
            case 25:
                checkResult(stack, "test_jenkins-master.44886e26-cf37-11e4-a1fc-56847afe9799");
                break;
            case 26:
                checkResult(stack, "test_jenkins-master.54a4936b-cf37-11e4-a1fc-56847afe9799");
                break;
            case 27:
                checkResult(stack, "test_jenkins-master.58d1e96c-cf37-11e4-a1fc-56847afe9799");
                break;
            case 28:
                checkResult(stack, "test_jenkins-master.5d97fcfd-cf37-11e4-a1fc-56847afe9799");
                break;
            case 29:
                checkResult(stack, "test_jenkins-master.6986f34f-cf37-11e4-a1fc-56847afe9799");
                break;
            case 30:
                checkResult(stack, "test_jenkins-master.790a33f1-cf37-11e4-a1fc-56847afe9799");
                break;
            case 31:
                checkResult(stack, "test_jenkins-master.8c2149b3-cf37-11e4-a1fc-56847afe9799");
                break;
            case 32:
                checkResult(stack, "test_jenkins-master.9c3d47e4-cf37-11e4-a1fc-56847afe9799");
                break;
            case 33:
                checkResult(stack, "test_jenkins-master.a06a01a6-cf37-11e4-a1fc-56847afe9799");
                break;
            case 34:
                checkResult(stack, "test_jenkins-master.fa821fbd-cf37-11e4-a1fc-56847afe9799");
                break;
            case 35:
                checkResult(stack, "test_jenkins-master.fbb39ade-cf37-11e4-a1fc-56847afe9799");
                break;
            case 36:
                checkResult(stack, "test_jenkins-master.ffe0f0e0-cf37-11e4-a1fc-56847afe9799");
                break;
            case 37:
                checkResult(stack, "test_jenkins-master.04a68f42-cf38-11e4-a1fc-56847afe9799");
                break;
            case 38:
                checkResult(stack, "test_jenkins-master.4ce2cc09-cf37-11e4-a1fc-56847afe9799");
                break;
            case 39:
                checkResult(stack, "test_jenkins-master.a792e469-cf37-11e4-a1fc-56847afe9799");
                break;
            case 40:
                checkResult(stack, "test_jenkins-master.fe166a0f-cf37-11e4-a1fc-56847afe9799");
                break;
            case 41:
                checkResult(stack, "test_jenkins-master.0670c7f3-cf38-11e4-a1fc-56847afe9799");
                break;
            case 42:
                checkResult(stack, "test_dev_frontend.3950b432-d234-11e4-bc5e-56847afe9799");
                break;
            case 43:
                checkResult(stack, "test_dv_helloworld.c3a7d253-d241-11e4-bc5e-56847afe9799");
                break;
            case 44:
                checkResult(stack, "test_dv_helloworld.d7c6b3c4-d244-11e4-bc5e-56847afe9799");
                break;
            case 45:
                checkResult(stack, "test_dev_frontend.a0f3f185-d2e6-11e4-bc5e-56847afe9799");
                break;
            }
            //} else {
        }
        recorder.resultFound(path);
    }

    public void resultFoundExit(Path path) {
        assert(path != idPath);
        recorder.resultFoundExit(path);
    }

    protected void checkResult(TokenStack stack, Object expected) {
        checkResult(stack.getStack().peek(), expected);
    }

    protected void checkResult(TokenStackElement elem, Object expected) {
        switch (elem.getType()) {
        case STRING_TOKEN:
        {
            StringToken token = (StringToken)elem;
            assert(token.value.equals(expected));
            break;
        }
        case FLOAT_TOKEN:
        {
            FloatToken token = (FloatToken)elem;
            assert(expected instanceof Float);
            assert(token.value == ((Float)expected).floatValue());
            break;
        }
        case INTEGER_TOKEN:
        {
            IntToken token = (IntToken)elem;
            assert(expected instanceof Integer);
            assert(token.value == ((Integer)expected).intValue());
            break;
        }
        case ARRAY_TOKEN:
        case OBJECT_TOKEN:
        {
            checkResult(elem.getValue(), expected);
            break;
        }
        default:
        {
            assert(false);
        }
        }
    }
}
