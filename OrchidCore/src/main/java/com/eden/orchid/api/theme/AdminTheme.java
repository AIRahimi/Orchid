package com.eden.orchid.api.theme;

import com.eden.orchid.api.OrchidContext;

import javax.inject.Inject;

public abstract class AdminTheme extends AbstractTheme {

    @Inject
    public AdminTheme(OrchidContext context, String key, int priority) {
        super(context, key, priority);
    }

}
