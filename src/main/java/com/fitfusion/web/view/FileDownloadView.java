package com.fitfusion.web.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class FileDownloadView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// model로 전달받은 File객체를 가져온다.
		File file = (File) model.get("file");
		String filename = file.getName();
		String originalFilename = filename.substring(36);
		String encodedFilename = URLEncoder.encode(originalFilename, "utf-8");
		
		// 응답정보 설정
		// 1. 응답컨텐츠 타입을 설정한다.
		//    application/octet-stream는 바이너리 데이터의 기본 컨텐츠 타입
		response.setContentType("application/octet-stream");
		// 2. 응답컨텐츠 길이를 설정한다.
		response.setContentLengthLong(file.length());
		// 3. 다운로드되는 파일명을 설정한다.
		//    "Content-Disposition"은 브라우저가 응답을 어떻게 처리할지 지시하는 정보를 설정
		//    "attachment; filename="a.txt""
		response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\";");
		
		// 파일을 읽어서 응답으로 보내기
		OutputStream out = response.getOutputStream();
		InputStream in = new FileInputStream(file);
		FileCopyUtils.copy(in, out);
	}
}












