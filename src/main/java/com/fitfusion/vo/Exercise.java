package com.fitfusion.vo;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("Exercise")
public class Exercise {
    private int exerciseId;
    private String exerciseName;
    private String description;
    private String category;
    private String equipment;
    private String mainParts;
    private int fatigueLevel;

}
