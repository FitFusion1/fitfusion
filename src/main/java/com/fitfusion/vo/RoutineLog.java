package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RoutineLog {
    private int routineLogId;
    private int routineId;
    private int logId;
    private int userId;
    private String name;
    private Date createdDate;
    private String routineType;
    private String conditionSnapshot;
    private Date performedDate;
}
