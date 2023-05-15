package com.wxy.mapper;

import com.wxy.domain.User;

import java.util.List;

public interface UserMapper {

    List<User> selectList();

    User selectOne(User user);
}
