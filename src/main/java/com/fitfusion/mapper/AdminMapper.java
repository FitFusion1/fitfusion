package com.fitfusion.mapper;

import com.fitfusion.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {

    /**
     * 유저 ID를 통해 해당 User 객체 조회
     * @param id 유저 아이디
     * @return 유저 객체 반환
     */
    User getUserById(int id);


    /**
     * 유저 ID를 통해 해당 User 객체 조회
     * @param id 유저 아이디
     * @return 유저 객체 반환
     */
    User getUserByIdWithRoleNames(int id);

    /**
     * 유저 ID를 통해 해당 유저의 roleName 리스트 조회
     * @param id
     * @return
     */
    List<String> getRoleNamesById(int id);

    /**
     * 모든 유저 정보를 반환한다.
     * @return User를 리스트 형식으로 반환한다.
     */
    List<User> getAllUsers();

    /**
     * 삭제되지 않은 사용자 수 조회
     * @return int값 반환
     */
    int countActiveUsers();

    /**
     * 삭제된 사용자 수 조회
     * @return int값 반환
     */
    int countDeletedUsers();

    /**
     * 금일 회원가입한 유저 수 조회
     * @return
     */
    int countTodayUsers();


    /**
     * 유저 논리 삭제
     * 해당 ID 유저의 컬럼 중 Deleted 값을 'Y'로 바꾼다.
     * @param id
     */
    void softDeleteUserById(int id);

    /**
     * 유저 논리 복구
     * 해당 ID 유저의 컬럼 중 Deleted 값을 'N'로 바꾼다.
     * @param id
     */
    void softRestoreUserById(int id);

    /**
     * 모든 공지사항을 조회한다.
     */
    List<Notice> getAllNotices();

    /**
     * 해당 ID의 공지사항을 조회한다.
     * @param id
     */
    Notice getNoticeById(int id);

    /**
     * notice 정보를 DB에 insert한다.
     * @param notice
     */
    void insertNotice(Notice notice);

    /**
     * 전체 공지사항의 개수를 조회한다.
     * @return
     */
    int countNotices();

    /**
     * 해당 ID의 공지사항을 삭제한다.
     * @param id
     */
    void deleteNoticeById(int id);

    /**
     * 공지사항을 수정한다.
     * @param notice
     */
    void modifyNotice(Notice notice);

    /**
     * 모든 비디오를 조회한다.
     * @return
     */
    List<Video> getAllVideos();

    /**
     * 해당 ID의 비디오를 조회한다.
     * @param id
     * @return
     */
    Video getVideoById(int id);

    /**
     * 해당 ID의 비디오와 유저를 조회한다.
     * @param id
     * @return
     */
    Video getVideoByIdWithUser(int id);

    /**
     * 해당 ID의 비디오와 유저와 운동을 조회한다.
     * @param id
     * @return
     */
    Video getVideoByIdWithUserAndExercises(int id);

    /**
     * 모든 운동 종목을 조회한다.
     * @return
     */
    List<Exercise> getAllExercises();

    /**
     * 모든 영상 카테고리를 조회한다.
     * @return
     */
    List<VideoCategory> getAllVideoCategories();

    /**
     * 운동 영상을 추가한다.
     * @param video
     */
    void insertVideo(Video video);

    /**
     * 비디오의 수를 조회한다.
     * @return
     */
    int countVideos();

    /**
     * 해당 아이디의 영상을 삭제한다.
     * @param id
     */
    void deleteVideoById(int id);

    /**
     * 해당 비디오의 영상을 업데이트한다.
     * @param video
     */
    void updateVideo(Video video);
}
