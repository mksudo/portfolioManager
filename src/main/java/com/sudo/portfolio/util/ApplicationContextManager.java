package com.sudo.portfolio.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class ApplicationContextManager implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext appContext) {
        ctx = appContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
}
