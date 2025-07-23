package com.fitfusion.mapper;

import com.fitfusion.vo.GymLikes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GymLikeMapper {


    void insertLike(GymLikes gymLikes);

    Integer selectLike(@Param("gymId") int gymId,@Param("userId") int userId);

    void deleteLike(@Param("gymId") int gymId,@Param("userId") int  userId);

    int countLike(int gymId);
}
