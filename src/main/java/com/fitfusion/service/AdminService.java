package com.fitfusion.service;

import com.fitfusion.mapper.AdminMapper;
import com.fitfusion.vo.*;
import com.fitfusion.web.form.AdminNoticeForm;
import com.fitfusion.web.form.AdminVideoForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    // C:/workspace/final_workspace/fitfusion/src/main/resources/static/videos
    @Value("${fitfusion.upload.video-path}")
    private String saveDirectory;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ModelMapper modelMapper;

    public User getUserByIdWithRoleNames(int id) {
        return adminMapper.getUserByIdWithRoleNames(id);
    }

    public User getUserById(int id) {
        return adminMapper.getUserById(id);
    }

    public List<User> getAllUsers() {
        return adminMapper.getAllUsers();
    }

    public int countActiveUsers() {
        return adminMapper.countActiveUsers();
    }

    public int countDeletedUsers() {
        return adminMapper.countDeletedUsers();
    }

    public int countTodayUsers() {
        return adminMapper.countTodayUsers();
    }

    public void softDeleteUserById(int no) {
        User user = adminMapper.getUserById(no);

        if (user == null){
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
        if (user.getUserId() != no) {
            throw new RuntimeException("다른 사람의 게시글은 삭제할 수 없습니다.");
        }

        adminMapper.softDeleteUserById(no);
    }

    public void softRestoreUserById(int no) {
        User user = adminMapper.getUserById(no);

        if (user == null){
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
        if (user.getUserId() != no) {
            throw new RuntimeException("다른 사람의 계정을 복구할 수 없습니다.");
        }

        adminMapper.softRestoreUserById(no);
    }

    public List<Notice> getAllNotices() {
        return adminMapper.getAllNotices();
    }

    public Notice getNoticeById(int id) {
        return adminMapper.getNoticeById(id);
    }

    public void insertNotice(AdminNoticeForm form, int id) {
        Notice notice = modelMapper.map(form, Notice.class);

        User user = new User();
        user.setUserId(id);

        notice.setUser(user);

        adminMapper.insertNotice(notice);
    }

    public int countNotices() {
        return adminMapper.countNotices();
    }

    public void deleteNoticeById(int id) {
        adminMapper.deleteNoticeById(id);
    }

    public void modifyNotice(AdminNoticeForm form, int id) {
        Notice notice = modelMapper.map(form, Notice.class);

        User user = new User();
        user.setUserId(id);

        notice.setUser(user);

        System.out.println("adminservice: " + notice.toString());

        adminMapper.modifyNotice(notice);
    }

    public List<Video> getAllVideos() {
        return adminMapper.getAllVideos();
    }

    public Video getVideoById(int id) {
        return adminMapper.getVideoById(id);
    }


    public void insertVideo(AdminVideoForm form, int userId) {
        Video video = modelMapper.map(form, Video.class);
        video.setUploadedBy(userId);

        // 동영상 첨부파일 업로드
        MultipartFile file = form.getFile();
        if (file != null){
            try {
                // 첨부파일명을 조회하고, Video객체에 담는다.
                String originalFilename = file.getOriginalFilename();
                String filename = UUID.randomUUID().toString() + originalFilename;
                video.setFilePath("/videos/" + filename);

                // 첨부파일을 지정된 디렉토리에 저장시킨다.
                File dest = new File(saveDirectory ,filename);
                file.transferTo(dest);
            } catch (IOException ex) {
                throw new RuntimeException("동영상 첨부파일 저장 중 오류가 발생했습니다.");
            }
        }

        // 썸네일 첨부파일 업로드
        MultipartFile image = form.getThumbnail();
        if (image != null){
            try {
                // 첨부파일명을 조회하고, Video객체에 담는다.
                String originalFilename = image.getOriginalFilename();
                String imageFilename = UUID.randomUUID().toString() + originalFilename;
                video.setThumbnailPath("/videos/" + imageFilename);

                // 첨부파일을 지정된 디렉토리에 저장시킨다.
                File dest = new File(saveDirectory, imageFilename);
                image.transferTo(dest);
            } catch (IOException ex) {
                throw new RuntimeException("이미지 첨부파일 저장 중 오류가 발생했습니다.");
            }
        }

        System.out.println("비디오 객체: " + video.toString());

        adminMapper.insertVideo(video);
    }

    public int countVideos() {
        return adminMapper.countVideos();
    }

//    public void deleteVideoById(int id) {
//        adminMapper.deleteVideoById(id);
//    }

    public void deleteVideoById(int id) {
        // 1. 삭제 대상 영상 정보 조회
        Video video = adminMapper.getVideoById(id);

        if (video != null) {
            // 2. 영상 파일 삭제
            String filePath = video.getFilePath(); // 예: "/videos/파일이름.mp4"
            if (filePath != null) {
                String filename = new File(filePath).getName(); // "파일이름.mp4"
                File videoFile = new File(saveDirectory, filename);
                if (videoFile.exists()) {
                    videoFile.delete();
                }
            }

            // 3. 썸네일 삭제
            String thumbPath = video.getThumbnailPath();
            if (thumbPath != null) {
                String thumbFilename = new File(thumbPath).getName();
                File thumbFile = new File(saveDirectory, thumbFilename);
                if (thumbFile.exists()) {
                    thumbFile.delete();
                }
            }

            // 4. DB에서 영상 정보 삭제
            adminMapper.deleteVideoById(id);
        } else {
            throw new RuntimeException("삭제할 영상이 존재하지 않습니다. ID: " + id);
        }
    }

    public void modifyVideo(AdminVideoForm form, User user, int videoId) {
        // 1. 기존 영상 조회
        Video video = adminMapper.getVideoById(videoId);
        if (video == null) {
            throw new RuntimeException("수정할 영상이 존재하지 않습니다. ID: " + videoId);
        }

        // 2. 텍스트 필드 업데이트
        video.setTitle(form.getTitle());
        video.setDescription(form.getDescription());
        video.setCategoryId(form.getCategoryId());
        video.setExerciseId(form.getExerciseId());
        video.setUploadedBy(user.getUserId());

        // 3. 새 영상 파일 처리
        MultipartFile newFile = form.getFile();
        if (newFile != null && !newFile.isEmpty()) {
            deletePhysicalFile(video.getFilePath());

            String newFilename = UUID.randomUUID() + newFile.getOriginalFilename();
            File dest = new File(saveDirectory, newFilename);
            try {
                newFile.transferTo(dest);
                video.setFilePath("/videos/" + newFilename);
            } catch (IOException e) {
                throw new RuntimeException("영상 파일 저장 중 오류", e);
            }
        }

        // 4. 새 썸네일 처리
        MultipartFile newThumb = form.getThumbnail();
        if (newThumb != null && !newThumb.isEmpty()) {
            deletePhysicalFile(video.getThumbnailPath());

            String newThumbName = UUID.randomUUID() + newThumb.getOriginalFilename();
            File dest = new File(saveDirectory, newThumbName);
            try {
                newThumb.transferTo(dest);
                video.setThumbnailPath("/videos/" + newThumbName);
            } catch (IOException e) {
                throw new RuntimeException("썸네일 저장 중 오류", e);
            }
        }

        // 5. DB 업데이트
        adminMapper.updateVideo(video);
    }

    private void deletePhysicalFile(String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(saveDirectory, new File(path).getName());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public Video getVideoByIdWithUser(int id) {
        return adminMapper.getVideoByIdWithUser(id);
    }

    public Video getVideoByIdWithUserAndExercises(int id){
        return adminMapper.getVideoByIdWithUserAndExercises(id);
    }

    public List<Exercise> getAllExercises(){
        return adminMapper.getAllExercises();
    }

    public List<VideoCategory> getAllVideoCategories() {
        return adminMapper.getAllVideoCategories();
    }

    public List<Exercise> getExercises() {
        return adminMapper.getAllExercises();
    }

    public int countExercises() {
        return adminMapper.countExercises();
    }

    public void deleteExerciseById(int no) {
        adminMapper.deleteExerciseById(no);
    }

    public void insertExercise(Exercise exercise) {
        adminMapper.insertExercise(exercise);
    }

    public Exercise getExerciseById(int id) {
        return adminMapper.getExerciseById(id);
    }

    public void modifyExercise(Exercise exercise) {

        adminMapper.updateExercise(exercise);
    }

    public int getActiveUserCount() {
        return adminMapper.countActiveUsers();
    }

    public int getDeletedUserCount() {
        return adminMapper.countDeletedUsers();
    }

    public int getTodayUserCount() {
        return adminMapper.countTodayUsers();
    }

    public int getVideoCount() {
        return adminMapper.countVideos();
    }

    public int getExerciseCount() {
        return adminMapper.countExercises();
    }

    public int getNoticeCount() {
        return adminMapper.countNotices();
    }
}
