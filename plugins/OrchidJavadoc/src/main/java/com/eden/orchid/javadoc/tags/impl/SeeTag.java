package com.eden.orchid.javadoc.tags.impl;

import com.eden.common.json.JSONElement;
import com.eden.orchid.javadoc.tags.api.JavadocBlockTagHandler;
import com.sun.javadoc.Tag;
import org.json.JSONArray;

import javax.inject.Inject;

public class SeeTag extends JavadocBlockTagHandler {

    @Inject
    public SeeTag() {
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
    public JSONElement processTags(Tag[] tags) {
        JSONArray array = new JSONArray();

        for(Tag tag : tags) {
            array.put(tag.text());
        }

        return new JSONElement(array);
    }
}
