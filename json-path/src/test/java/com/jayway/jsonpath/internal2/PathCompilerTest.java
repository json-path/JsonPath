package com.jayway.jsonpath.internal2;

public class PathCompilerTest {

    /*
    @Test
    public void a_path_can_be_formalized() {

        assertEquals("$[*]['category', 'title']", PathCompiler.compile("$[*].['category', 'title']").getPath());
        assertEquals("$['foo'][*]", PathCompiler.compile("foo.*").getPath());
        assertEquals("$[?(@.decimal == 0.1 && @.int == 1)]", PathCompiler.compile("$[?(@.decimal == 0.1 && @.int == 1)]").getPath());
        assertEquals("$['foo']['bar'][?(@.foo == 'bar')]['prop']", PathCompiler.compile("$.foo.bar[?(@.foo == 'bar')].prop").getPath());
        assertEquals("$['foo','bar']['bar'][1,2,3]", PathCompiler.compile("['foo','bar'].bar[1,2,3]").getPath());
        assertEquals("$['foo','bar']['bar'][-1:]", PathCompiler.compile("['foo','bar'].bar[-1:]").getPath());
        assertEquals("$['foo','bar']['bar'][1]", PathCompiler.compile("['foo','bar'].bar[1]").getPath());
        assertEquals("$['foo','bar']['bar'][:1]", PathCompiler.compile("['foo','bar'].bar[:1]").getPath());
        assertEquals("$['foo','bar']['bar'][0:5]", PathCompiler.compile("['foo','bar'].bar[0:5]").getPath());
        assertEquals("$['foo']..['bar'][1]", PathCompiler.compile("foo..bar[1]").getPath());
        assertEquals("$['foo']['bar'][1]", PathCompiler.compile("foo.bar[1]").getPath());
        assertEquals("$['foo']['bar'][1]", PathCompiler.compile("$.foo.bar[1]").getPath());
        assertEquals("$['foo']['bar'][1,2,3]", PathCompiler.compile("$.foo.bar[1,2,3]").getPath());
        assertEquals("$['foo']['bar'][1]['prop']", PathCompiler.compile("$.foo.bar[1].prop").getPath());
        assertEquals("$['foo']['fiz']['bar'][1]", PathCompiler.compile("$.foo['fiz'].bar[1]").getPath());
        assertEquals("$['foo']['fiz']['bar'][1]['next']", PathCompiler.compile("$.foo['fiz'].bar[1].next").getPath());
        assertEquals("$['foo']['fiz']['bar'][1]['next']", PathCompiler.compile("$['foo']['fiz']['bar'][1]['next']").getPath());
        assertEquals("$['foo']['bar'][?(@['foo'] == 'bar')]['prop']", PathCompiler.compile("$.foo.bar[?(@['foo'] == 'bar')].prop").getPath());
    }

    @Test(expected = InvalidPathException.class)
    public void function_brackets_must_match() {
        PathCompiler.compile("$[?(@.decimal == 0.1 && @.int == 1]");
    }

    @Test
    public void property_fragment_can_be_analyzed() {
        PathCompiler.PathComponentAnalyzer.analyze("['foo','bar']");
    }

    @Test
    public void number_fragment_can_be_analyzed() {

        PathCompiler.PathComponentAnalyzer.analyze("[*]");
        PathCompiler.PathComponentAnalyzer.analyze("[(@.size() - 1)]");
        PathCompiler.PathComponentAnalyzer.analyze("[(@.length-2)]");
        PathCompiler.PathComponentAnalyzer.analyze("[1]");
        PathCompiler.PathComponentAnalyzer.analyze("[1,2,3]");
        PathCompiler.PathComponentAnalyzer.analyze("[:1]");
        PathCompiler.PathComponentAnalyzer.analyze("[-1:]");
        PathCompiler.PathComponentAnalyzer.analyze("[0:1]");
    }

    @Test
    public void criteria_can_be_analyzed() {

        PathCompiler.PathComponentAnalyzer.analyze("[?(@ == 'bar' && @.size == 1)]");
        PathCompiler.PathComponentAnalyzer.analyze("[?(@.foo)]");
        PathCompiler.PathComponentAnalyzer.analyze("[ ?(@.foo) ]");
        PathCompiler.PathComponentAnalyzer.analyze("[ ?( @.foo ) ]");
        PathCompiler.PathComponentAnalyzer.analyze("[?(@.foo)]");
        PathCompiler.PathComponentAnalyzer.analyze("[?(@.foo == 'bar')]");
        PathCompiler.PathComponentAnalyzer.analyze("[?(@['foo']['bar'] == 'bar')]");
        PathCompiler.PathComponentAnalyzer.analyze("[?(@ == 'bar')]");
    }
    */
}
