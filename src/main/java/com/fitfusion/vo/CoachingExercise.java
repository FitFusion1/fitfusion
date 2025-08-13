package com.fitfusion.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@NoArgsConstructor
@Alias("CoachingExercise")
public class CoachingExercise {

    private int id;
    private String title;
    private String name;
    private String description;
    private String tips;
    private int repTime;
    private String previewImagePath;

}
