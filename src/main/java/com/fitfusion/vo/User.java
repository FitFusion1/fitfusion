package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Alias("User")
public class User {
    private int userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String gender;
    private int height;
    private int weight;
    private Date birthDate;
    private Date createdDate;
    private Date updatedDate;
    private String deleted;
    private Date deletedDate;
    private String experienceLevel;

    private List<String> roleNames;
}
