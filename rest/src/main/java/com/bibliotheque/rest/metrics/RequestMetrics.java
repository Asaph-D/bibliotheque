package com.bibliotheque.rest.metrics;

public final class RequestMetrics {
    private RequestMetrics() {}

    private static final ThreadLocal<Long> INTERNAL_CALLS = ThreadLocal.withInitial(() -> 0L);

    public static void reset() {
        INTERNAL_CALLS.set(0L);
    }

    public static void incInternalCall() {
        INTERNAL_CALLS.set(INTERNAL_CALLS.get() + 1);
    }

    public static long internalCalls() {
        return INTERNAL_CALLS.get();
    }

    public static void clear() {
        INTERNAL_CALLS.remove();
    }
}

