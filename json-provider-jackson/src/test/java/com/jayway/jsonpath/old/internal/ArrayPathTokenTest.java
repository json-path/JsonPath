package com.jayway.jsonpath.old.internal;

import static com.jayway.jsonpath.JsonPath.read;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class ArrayPathTokenTest extends TestBase {



    @Test
    public void array_can_select_multiple_indexes() {

        List result = read(ARRAY, "$[0,1]");

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(new ArrayList() {{
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-0");
        		}});
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-1");
        		}});
        }});
    }

    @Test
    public void array_can_be_sliced_to_2() {

        List result = read(ARRAY, "$[:2]");
        assertThat(result).isEqualTo(new ArrayList() {{
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-0");
        		}});
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-1");
        		}});
        }});

    }

    @Test
    public void array_can_be_sliced_to_2_from_tail() {

        List result = read(ARRAY, "$[:-5]");
        assertThat(result).isEqualTo(new ArrayList() {{
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-0");
        		}});
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-1");
        		}});
        }});
    }

    @Test
    public void array_can_be_sliced_from_2() {

        List result = read(ARRAY, "$[5:]");
        assertThat(result).isEqualTo(new ArrayList() {{
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-5");
        		}});
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-6");
        		}});
        }});
    }

    @Test
    public void array_can_be_sliced_from_2_from_tail() {

        List result = read(ARRAY, "$[-2:]");
        assertThat(result).isEqualTo(new ArrayList() {{
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-5");
        		}});
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-6");
        		}});
        }});

    }

    @Test
    public void array_can_be_sliced_between() {

        List result = read(ARRAY, "$[2:4]");
        assertThat(result).isEqualTo(new ArrayList() {{
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-2");
        		}});
        	this.add(
        		new HashMap() {{
        			this.put("foo",  "foo-val-3");
        		}});
        }});
    }
}
