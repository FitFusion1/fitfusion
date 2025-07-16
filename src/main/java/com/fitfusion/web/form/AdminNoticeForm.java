package com.fitfusion.web.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdminNoticeForm {
    private int noticeId;
    private String title;
    private String content;
}
