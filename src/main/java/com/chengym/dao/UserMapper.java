package com.chengym.dao;

import com.chengym.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * desc:
 *
 * @param
 * @author chengym
 * @return
 * @date 2018/12/03 0:06
 */
@Mapper
@Repository
public interface UserMapper {

    int insert(User user);

    User getById(@Param("id")String id);

    List<User> getAll(@Param("source")int source);

    List<User> getALlForPage(@Param("start")int start , @Param("end")int end,@Param("source")int source);

    User getByUserId(String userId);

    int update(User user);

    List<User> getUsers();
}
