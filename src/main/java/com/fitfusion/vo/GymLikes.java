package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@Alias( "GymLikes")
public class GymLikes {

    private int likeId;
    private Date createdDate;
    private Date updatedDate;
    private int userId;
    private int gymId;

    private User user;
    private Gym gym;
}
