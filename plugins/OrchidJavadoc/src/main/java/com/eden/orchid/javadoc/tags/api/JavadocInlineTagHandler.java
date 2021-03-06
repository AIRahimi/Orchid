package com.eden.orchid.javadoc.tags.api;

import com.eden.common.json.JSONElement;
import com.eden.orchid.api.registration.Prioritized;
import com.sun.javadoc.Tag;

/**
 * A JavadocBlockTagHandler processes Tags within Javadoc comments.
 */
public abstract class JavadocInlineTagHandler extends Prioritized {

    public JavadocInlineTagHandler(int priority) {
        super(priority);
    }

    public abstract JSONElement processTag(Tag tag);

    public abstract String getName();

    public abstract String getDescription();

}
