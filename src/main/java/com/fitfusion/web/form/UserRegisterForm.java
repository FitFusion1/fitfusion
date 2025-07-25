package com.fitfusion.web.form;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterForm {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min =  3, max = 12, message = "아이디는 최소 3글자 이상 입력해주세요.")
    @Pattern(regexp = "^[0-9a-zA-Z_-]+$", message = "영어 대소문자와 '_', '-'만 사용 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 6, message = "비밀번호는 6글자 이상 입력해주세요.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, message = "이름은 두 글자 이상 입력해주세요.")
    @Pattern(regexp = "^[가-힣]+$", message = "한국어 이름을 입력해주세요.")
    private String name;

    @NotNull(message = "생일은 필수 입력값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate = LocalDate.of(2000, 1, 1);

    @NotBlank(message = "성별은 필수 선택사항입니다.")
    private String gender;

    @NotNull(message = "키는 필수 입력값입니다.")
    @Min(value = 80, message = "올바른 값을 입력해주세요.")
    @Max(value = 222, message = "올바른 값을 입력해주세요.")
    private Integer height = null;

    @NotNull(message = "몸무게는 필수 입력값입니다.")
    @Min(value = 20, message = "올바른 값을 입력해주세요.")
    @Max(value = 500, message = "올바른 값을 입력해주세요.")
    private Integer weight = null;

    @NotBlank(message = "숙련도는 필수 선택사항입니다.")
    private String experienceLevel;

}
