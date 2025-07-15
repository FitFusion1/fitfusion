package com.fitfusion.vo;

import lombok.Data;

import java.util.Date;

@Data
public class Condition {

    private int conditionId;
    private int userId;
    private String conditionLevel;
    private Date createdDate;
    private String note;
}
