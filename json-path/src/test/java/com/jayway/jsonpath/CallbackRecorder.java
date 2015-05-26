package com.jayway.jsonpath;

import java.util.List;
import java.util.ArrayList;

import com.jayway.jsonpath.EvaluationCallback;
import com.jayway.jsonpath.internal.Path;

public class CallbackRecorder implements EvaluationCallback {

    public static class CallbackEvent {
        private Path path;
        private boolean exit;
        public CallbackEvent(Path path, boolean exit) {
            this.path = path;
            this.exit = exit;
        }

        public Path getPath() {
            return path;
        }

        public boolean isExit() {
            return exit;
        }

        public boolean equals(Object o) {
            if (o instanceof CallbackEvent) {
                CallbackEvent other = (CallbackEvent)o;
                return other.getPath() == path && other.isExit() == exit;
            }
            return false;
        }
    }

    private List<CallbackEvent> results;

    public CallbackRecorder() {

        results = new ArrayList<CallbackEvent>();
    }

    public void resultFound(Path path) {
        System.err.println("found result " + path);
        results.add(new CallbackEvent(path, false));
    }

    public void resultFoundExit(Path path) {
        System.err.println("exiting result " + path);
        results.add(new CallbackEvent(path, true));
    }

    public List<CallbackEvent> getResults() {
        return results;
    }
}
