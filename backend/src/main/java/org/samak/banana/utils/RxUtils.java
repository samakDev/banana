package org.samak.banana.utils;

import io.reactivex.functions.Consumer;
import org.slf4j.Logger;

public final class RxUtils {

    private RxUtils() {
    }

    public static Consumer<? super Throwable> logError(final Logger logger) {
        return e -> logger.error("Exception is :", e);
    }

}
