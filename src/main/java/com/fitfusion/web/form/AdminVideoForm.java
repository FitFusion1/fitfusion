package com.fitfusion.web.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class AdminVideoForm {
    private String title;
    private String description;
    private MultipartFile file;
    private MultipartFile thumbnail;
    private int categoryId;
    private int exerciseId;

}
