package com.fitfusion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Alias("CoachingExerciseDto")
public class CoachingExerciseDto {

    private int id;
    private String title;
    private String name;
    private String description;
    private List<String> tips;
    private int repTime;
    private String previewImagePath;

}
