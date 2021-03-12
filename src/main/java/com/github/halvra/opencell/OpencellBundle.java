package com.github.halvra.opencell;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

public class OpencellBundle extends AbstractBundle {
    @NonNls
    private static final String BUNDLE = "messages.OpencellBundle";
    private static final OpencellBundle INSTANCE = new OpencellBundle();

    public OpencellBundle() {
        super(BUNDLE);
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

    public static Supplier<String> messagePointer(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getLazyMessage(key, params);
    }
}
