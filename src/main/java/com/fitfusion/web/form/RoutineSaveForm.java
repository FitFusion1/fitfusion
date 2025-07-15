package com.fitfusion.web.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoutineSaveForm {
    private String name;
    private String difficultLevel;
    private String description;
    private List<Integer> exerciseId;
}
