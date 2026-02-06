package com.buuz135.simpleclaims.papi;

import com.hypixel.hytale.logger.HytaleLogger;

import java.lang.reflect.Method;

public final class PAPIIntegration {
    private static final HytaleLogger LOGGER = HytaleLogger.getLogger().getSubLogger("SimpleClaims");;

    private PAPIIntegration() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void register() {
        try {
            final Class<?> papiClass = Class.forName("at.helpch.placeholderapi.PlaceholderAPI");

            final Class<?> expansionClass = Class.forName("com.buuz135.simpleclaims.papi.SimpleClaimsExpansion");
            final Object expansion = expansionClass.getConstructor().newInstance();
            final Method register = expansionClass.getMethod("register");
            register.invoke(expansion);
            LOGGER.atInfo().log("[PlaceholderAPI] Found, registering SimpleClaims expansion.");
        } catch (Exception e) {
            LOGGER.atInfo().log("[PlaceholderAPI] Not found, SimpleClaims expansion will not be loaded.");
        }
    }
}
