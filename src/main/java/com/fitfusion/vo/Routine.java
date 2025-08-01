package com.fitfusion.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Routine {
    private int routineId;
    private int userId;
    private String name;
    private String difficultyLevel = "중";
    private Date createdDate;
    private Date updatedDate;
    private String description;
}
