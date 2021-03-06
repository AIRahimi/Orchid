package com.eden.orchid.javadoc.tags.impl;

import com.eden.common.json.JSONElement;
import com.eden.orchid.javadoc.tags.api.JavadocInlineTagHandler;
import com.sun.javadoc.Tag;

import javax.inject.Inject;

public class LinkTag extends JavadocInlineTagHandler {

    @Inject
    public LinkTag() {
        super(40);
    }

    @Override
    public String getName() {
        return "see";
    }

    @Override
    public String getDescription() {
        return "Create a link to another document indexed by Orchid. Not limited to Javadoc links.";
    }

    @Override
    public JSONElement processTag(Tag tag) {
        return new JSONElement(tag.text());
    }
}
