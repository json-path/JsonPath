package com.jayway.jsonpath;

/**
 * A listener that can be registered on a {@link com.jayway.jsonpath.Configuration} that is notified when a
 * result is added to the result of this path evaluation.
 */
public interface EvaluationListener {

    /**
     * Callback invoked when result is found
     * @param found the found result
     * @return continuation instruction
     */
    EvaluationContinuation resultFound(FoundResult found);

    public static enum EvaluationContinuation {
        /**
         * Evaluation continues
         */
        CONTINUE,
        /**
         * Current result is included but no further evaluation will be performed.
         */
        ABORT
    }

    /**
     *
     */
    public interface FoundResult {
        /**
         * the index of this result. First result i 0
         * @return index
         */
        int index();

        /**
         * The path of this result
         * @return path
         */
        String path();

        /**
         * The result object
         * @return the result object
         */
        Object result();
    }
}
