package com.github.halvra.opencell.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ScriptInstanceDto {
    String code;
    String description;
    String type;
    String script;

    public ScriptInstanceDto() {
        this.type = "JAVA";
    }
}