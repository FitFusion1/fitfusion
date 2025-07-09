package com.fitfusion.web.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserRegisterForm {

    private String username;
    private String password;
    private String email;
    private String name;
    private Date birthDate;
    private String gender;
    private int height;
    private int weight;
    private String experienceLevel;

}
