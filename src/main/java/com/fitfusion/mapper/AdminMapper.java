package com.fitfusion.mapper;

import com.fitfusion.vo.User;
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

}
