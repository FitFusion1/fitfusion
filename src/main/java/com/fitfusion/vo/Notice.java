package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@ToString
@Alias("Notice")
public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private Date createdDate;
    private Date updatedDate;
    private User user;
}
