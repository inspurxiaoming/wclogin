package com.chengym.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chengym.bean.UserInfo;
import com.chengym.bean.UserLogin;
import com.chengym.common.CommonConstants;
import com.chengym.common.IMoocJSONResult;
import com.chengym.common.RedisOperator;
import com.chengym.common.TimeUtils;
import com.chengym.dao.UserMapper;
import com.chengym.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * desc:
 *
 * @param
 * @author chengym
 * @return
 * @date 2018/12/03 0:03
 */
@Service
@Slf4j
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private RedisOperator redis;
    public User getUserById(String id){
        return userMapper.getById(id);
    }

    public int addUser(User user) {
        return userMapper.insert(user);
    }

    public IMoocJSONResult getUserByUserId(String userId) {
        IMoocJSONResult iMoocJSONResult = new IMoocJSONResult();
        //根据userId获取用户的详情
        User  user = userMapper.getByUserId(userId);
        if(user!=null){
            if(StringUtils.isNotEmpty(redis.get("user-redis-session:" + user.getId()))){
                iMoocJSONResult.setStatus(200);
                iMoocJSONResult.setMsg("OK");
                iMoocJSONResult.setRoles(StringUtils.isEmpty(user.getRoles())?"user":user.getRoles());
                iMoocJSONResult.setManageUrl(StringUtils.isEmpty(user.getRoles())? CommonConstants.USER_INTRODUCE_PAGE:(user.getRoles().contains("admin")?CommonConstants.USER_INDEX_PAGE:CommonConstants.USER_INTRODUCE_PAGE));
            }else{
                log.info("user {} serssion error");
                iMoocJSONResult.setStatus(200);
                iMoocJSONResult.setMsg("user Session error");
                iMoocJSONResult.setRoles("user");
                iMoocJSONResult.setManageUrl(CommonConstants.USER_LOGIN_PAGE);
            }
        }else{
            iMoocJSONResult.setStatus(400);
            iMoocJSONResult.setMsg("NO USER");
            iMoocJSONResult.setRoles("user");
            iMoocJSONResult.setManageUrl(CommonConstants.USER_LOGIN_PAGE);
        }
        //判断redis中用户是否有效
        //返回检查结果，有效返回用户跳转页面，否则返回session失效

        return iMoocJSONResult;
    }

    public User getUserByUserIdResult(String userId) {
        //根据userId获取用户的详情
        User  user = userMapper.getByUserId(userId);

        return user;
    }


    public IMoocJSONResult getAllUsers(int start,int length,int source) {
        IMoocJSONResult iMoocJSONResult = new IMoocJSONResult();
        iMoocJSONResult.setData(userMapper.getALlForPage(start,start+length,source));
        iMoocJSONResult.setTotal(userMapper.getAll(source).size());
        return iMoocJSONResult;
    }

    public IMoocJSONResult updateUser(User user) {
        IMoocJSONResult iMoocJSONResult = new IMoocJSONResult();
        int result =  userMapper.update(user);
        if (1==result){
            iMoocJSONResult.setStatus(200);
            iMoocJSONResult.setMsg("user set admin success");
        }else {
            iMoocJSONResult.setStatus(400);
            iMoocJSONResult.setMsg("user Session error");
        }
        return iMoocJSONResult;
    }

    public UserInfo getUserFromRawData(String rawData){
        JSONObject jsonObj = (JSONObject) JSON.parse(rawData);
        UserInfo userInfo= JSONObject.toJavaObject(jsonObj,UserInfo.class);
        return userInfo;
    }

    public IMoocJSONResult getUsers() {
        IMoocJSONResult iMoocJSONResult = new IMoocJSONResult();
        iMoocJSONResult.setData(userMapper.getUsers());
        return iMoocJSONResult;
    }

    public User getUserInfo(String userOpenId, Map<String, String> userInfoMap, UserLogin userLogin){
        User user = new User();
        user.setId(userOpenId);
        user.setPhone(userInfoMap.get("phoneNumber"));
        user.setName(userLogin.getUserInfo().getNickName());
        user.setUserId(userLogin.getUserId());
        //正常环境用户角色设置
        user.setRoles("user");
        //审核环境用户角色设置
//            user.setRoles(CommonConstants.USER_ROLES_ADMIN);
        user.setCreatedTime(TimeUtils.longToDate(System.currentTimeMillis()));
        if(StringUtils.isNoneEmpty(userLogin.getRawData())){
            userLogin.setUserInfo(getUserFromRawData(userLogin.getRawData()));
        }
        user.setAvatarUrl(userLogin.getUserInfo().getAvatarUrl());
        user.setSource(1);
        return user;
    }
}
