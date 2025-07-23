package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@ToString
@Alias("VideoCategory")
public class VideoCategory {
    private int categoryId;
    private String name;
    
}
