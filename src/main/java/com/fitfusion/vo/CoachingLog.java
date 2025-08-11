package com.fitfusion.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Alias("CoachingLog")
public class CoachingLog {

    private int id;
    private int userId;
    private String exerciseId;
    private int targetSets;
    private int targetReps;
    private int performedSets;
    private int performedReps;
    private int goodReps;
    private int targetTime;
    private int performedTime;
    private int goodTime;
    private double accuracyPercent;
    private int duration;
    private Date performedDate;

}
