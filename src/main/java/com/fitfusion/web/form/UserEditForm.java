package com.fitfusion.web.form;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UserEditForm {

    private int userId;
    private String username;
    private String password;
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;
    private String gender;

    @NotNull(message = "키는 필수 입력값입니다.")
    @Min(value = 80, message = "올바른 값을 입력해주세요.")
    @Max(value = 222, message = "올바른 값을 입력해주세요.")
    private Integer height;

    @NotNull(message = "몸무게는 필수 입력값입니다.")
    @Min(value = 20, message = "올바른 값을 입력해주세요.")
    @Max(value = 500, message = "올바른 값을 입력해주세요.")
    private Integer weight;

    @NotNull(message = "생일은 필수 입력값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @NotBlank(message = "숙련도는 필수 선택사항입니다.")
    @Pattern(
            regexp = "^(beginner|intermediate|advanced)$",
            message = "초급, 중급, 고급 중에 선택해주세요."
    )
    private String experienceLevel;

}
