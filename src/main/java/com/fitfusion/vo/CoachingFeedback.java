package com.fitfusion.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@NoArgsConstructor
@Alias("CoachingFeedback")
public class CoachingFeedback {

    private int id;
    private int coachingLogId;
    private String issueCode;
    private String description;
    private int setNo;
    private int repNo;
    private int timestamp;

}
