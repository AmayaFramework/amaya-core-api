package io.github.amayaframework.core.pipeline;

interface CompletableData {
    /**
     * Explicitly indicates that further data modification is not possible.
     */
    void complete();

    /**
     * @return completion status: true if completed, false if not
     */
    boolean isCompleted();
}
