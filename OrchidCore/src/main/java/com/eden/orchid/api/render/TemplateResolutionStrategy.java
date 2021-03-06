package com.eden.orchid.api.render;

import com.eden.orchid.api.registration.Prioritized;
import com.eden.orchid.api.theme.components.OrchidComponent;
import com.eden.orchid.api.theme.pages.OrchidPage;

import java.util.List;

public abstract class TemplateResolutionStrategy extends Prioritized {

    public TemplateResolutionStrategy(int priority) {
        super(priority);
    }

    public abstract List<String> getPageLayout(OrchidPage page);

    public abstract List<String> getPageTemplate(OrchidPage page);

    public abstract List<String> getComponentTemplate(OrchidComponent component);

}
