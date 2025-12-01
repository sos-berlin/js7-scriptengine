package com.sos.js7.scriptengine.jobs.commons;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "options" })
public class ScriptJobOptionsJson {

    @JsonProperty("options")
    @JsonDeserialize(as = java.util.LinkedHashMap.class)
    private Map<String, String> options = new LinkedHashMap<>();

    @JsonProperty("options")
    public Map<String, String> getOptions() {
        return options;
    }

    @JsonProperty("options")
    public void setOptions(Map<String, String> val) {
        options = val;
    }

}
