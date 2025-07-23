package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@ToString
@Alias("Video")
public class Video {
    private int videoId;
    private String title;
    private String description;
    private String filePath;
    private String thumbnailPath;
    private int duration;
    private Date uploadDate;
    private int uploadedBy;
    private int categoryId;
    private int exerciseId;

    // 조인관련 객체
    private User user;
    private VideoCategory videoCategory;
    private Exercise exercise;

}
