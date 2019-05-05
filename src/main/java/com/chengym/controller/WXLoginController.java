package com.chengym.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.chengym.bean.UserAdmin;
import com.chengym.bean.UserLogin;
import com.chengym.common.*;
import com.chengym.model.User;
import com.chengym.service.ProxyService;
import com.chengym.service.UserService;
import com.chengym.utils.encryptrd.AesCbcUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chengym.model.WXSessionModel;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class WXLoginController {

    @Autowired
    private RedisOperator redis;
    @Autowired
    UserService userService;
    @Autowired
    ProxyService proxyService;

    @PostMapping("/wx-server/wcLogin")
    public IMoocJSONResult wxLogin(@RequestBody UserLogin userLogin) throws Exception {
        log.info("++userCode:{}", userLogin.getCode());
        log.info("++userEncryptedData:{}", userLogin.getEncryptedData());
        log.info("++userIv:{}", userLogin.getIv());
        log.info("++userRawData:{}", userLogin.getRawData());
        WXSessionModel model = getWXSessionModel(userLogin);
        // 存入session到redis
        redis.set("user-redis-session:" + model.getOpenid(),
                model.getSession_key());
        User user = userService.getUserById(model.getOpenid());
        if(user == null){
            UUID uuid = UUID.randomUUID();
            String uuidCode = uuid.toString();
            if(StringUtils.isEmpty(userLogin.getUserId())){
                userLogin.setUserId(uuidCode);
            }
            user = setUserInfo(userLogin,model);
//        }else{
//            checkUserForDiff(user,userLogin.getRawData());
        }
        IMoocJSONResult iMoocJSONResult = new IMoocJSONResult(userLogin.getUserInfo().getAvatarUrl(), user==null?"user":user.getRoles());
        iMoocJSONResult.setUserId(user.getUserId());
        if(iMoocJSONResult!=null){
            log.info("user will see {}",iMoocJSONResult.getManageUrl() );
        }
        return iMoocJSONResult;
    }

    private User setUserInfo(UserLogin userLogin, WXSessionModel model) throws Exception {
        User user = new User();
        String decodeEncryptedData = decode(userLogin.getEncryptedData());
        String userInfo = null;
        String userOpenId = model.getOpenid();
        String sessionKey = model.getSession_key();
        decodeEncryptedData = decodeEncryptedData.replaceAll(" ","\\+");
        userInfo = AesCbcUtil.decrypt(decodeEncryptedData, sessionKey, userLogin.getIv(), "UTF-8");
        log.info("uuuuuserInfo{}",userInfo);
        if (StringUtils.isNotEmpty(userInfo)) {
            Map<String, String> userInfoMap = (new ObjectMapper()).readValue(userInfo, Map.class);
            user = userService.getUserInfo(userOpenId,userInfoMap,userLogin);
            userService.addUser(user);
        }
        return user;
    }


    @PostMapping("/wx-server/detail")
    public IMoocJSONResult getUserMessage(@RequestBody UserLogin userLogin) {
        log.info("+++detail get userId :{} ",userLogin.getUserId());
        IMoocJSONResult iMoocJSONResult = userService.getUserByUserId(userLogin.getUserId());
        return iMoocJSONResult;
    }

    @Value("${wechat.appid}")
    String appid="wx8099953f3ba7d072";
    @Value("${wechat.secret}")
    String secret="5da2451cc68d32f35bd364389ccd52a1";
    private WXSessionModel getWXSessionModel(UserLogin userLogin){
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> param = new HashMap<>();
//        param.put("appid", "wx8c0e94d2e3a3d4d4");
        param.put("appid", appid);
//        param.put("secret", "10f8d287ee2c99c4b07b03b266515a36");
        param.put("secret", secret);
        param.put("js_code", userLogin.getCode());
        param.put("grant_type", "authorization_code");

        String wxResult = HttpClientUtil.doGet(url, param);
        log.info("++==userWxResult:{}",wxResult);
        WXSessionModel model = JsonUtils.jsonToPojo(wxResult, WXSessionModel.class);
        log.info("++##userModel-session_key:{}", model != null ? model.getSession_key() : "");
        log.info("++##userModel-openid:{}", model != null ? model.getOpenid() : "");
        return model;
    }
    private String decode(String url){
        try {
            String prevURL="";
            String decodeURL=url;
            while(!prevURL.equals(decodeURL))
            {
                prevURL=decodeURL;
                decodeURL= URLDecoder.decode( decodeURL, "UTF-8" );
            }
            return decodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Issue while decoding" +e.getMessage();
        }
    }
    @PostMapping("/wx-server/get")
    public IMoocJSONResult getUserInfo(@RequestBody UserLogin userLogin) throws Exception {
        log.info("++userId:{}", userLogin.getUserId());
        log.info("++userEncryptedData:{}", userLogin.getEncryptedData());
        log.info("++userIv:{}", userLogin.getIv());
        log.info("++userRawData:{}", userLogin.getRawData());
        WXSessionModel model = getWXSessionModel(userLogin);
        // 存入session到redis
        redis.set("user-redis-session:" + model.getOpenid(),
                model.getSession_key());
        User user = userService.getUserById(model.getOpenid());
        if(user == null){
            user = setUserInfo(userLogin,model);
        }
        IMoocJSONResult iMoocJSONResult = new IMoocJSONResult(userLogin.getUserInfo().getAvatarUrl(), user==null?"user":user.getRoles());
        return iMoocJSONResult;
    }
    @GetMapping("/wx-server/list")
    public IMoocJSONResult getAllUsers( @RequestParam(required = false, defaultValue = "1") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length){
        return userService.getAllUsers(start, length,1);
    }
    @GetMapping("/wx-server/old-list")
    public IMoocJSONResult getAllOldUsers( @RequestParam(required = false, defaultValue = "1") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length){
        return userService.getAllUsers(start, length,0);
    }
    @PutMapping("/wx-server/{userId}/set-admin")
    public IMoocJSONResult update(@PathVariable("userId")String userId){
        User user = userService.getUserByUserIdResult(userId);
        user.setUserId(userId);
        user.setRoles(CommonConstants.USER_ROLES_ADMIN);
        return userService.updateUser(user);
    }
    @PutMapping("/wx-server/{userId}/set-user")
    public IMoocJSONResult updateSetUser(@PathVariable("userId")String userId){
        User user = userService.getUserByUserIdResult(userId);
        user.setUserId(userId);
        user.setRoles(CommonConstants.USER_ROLES_USER);
        return userService.updateUser(user);
    }
    @GetMapping("/wx-server/user-list")
    public IMoocJSONResult getUsers( @RequestParam(required = false, defaultValue = "1") int start,
                                     @RequestParam(required = false, defaultValue = "10") int length){
        return userService.getUsers();
    }
    @GetMapping("/wx-server/user-old-list")
    public IMoocJSONResult getOldUsers( @RequestParam(required = false, defaultValue = "1") int start,
                                     @RequestParam(required = false, defaultValue = "10") int length){
        return userService.getUsers();
    }
    @GetMapping("/wx-server/customer")
    public ResponseBean getCustomer(){
        return proxyService.getCustomer();
    }
    @GetMapping("/wx-server/trade")
    public ResponseBean getTrade(){
        return proxyService.getTrade();
    }
    @GetMapping("/wx-server/resource")
    public ResponseBean getResource(){
        return proxyService.getResource();
    }
    @GetMapping("/wx-server/site")
    public ResponseBean getSite(){
        return proxyService.getSite();
    }
}
