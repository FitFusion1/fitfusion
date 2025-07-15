package com.fitfusion.web.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterForm {

    private String username;
    private String password;
    private String email;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate = LocalDate.of(2000, 1, 1);
    private String gender;
    private Integer height = null;
    private Integer weight = null;
    private String experienceLevel;

}
