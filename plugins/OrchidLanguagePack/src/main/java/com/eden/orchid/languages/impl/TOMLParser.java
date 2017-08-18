package com.eden.orchid.languages.impl;

import com.eden.orchid.api.compilers.OrchidParser;
import com.moandjiezana.toml.Toml;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class TOMLParser extends OrchidParser {

    @Override
    public String getDelimiter() {
        return Pattern.quote(getDelimiterString());
    }

    @Override
    public String getDelimiterString() {
        return "+";
    }

    @Override
    public String[] getSourceExtensions() {
        return new String[] {"tml", "toml"};
    }

    @Override
    public JSONObject parse(String extension, String input) {
        Toml toml = new Toml().read(input);
        return new JSONObject(toml.toMap());
    }
}