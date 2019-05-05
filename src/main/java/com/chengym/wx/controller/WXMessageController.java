package com.chengym.wx.controller;

import com.chengym.bean.UserAdmin;
import com.chengym.bean.UserLogin;
import com.chengym.common.*;
import com.chengym.model.User;
import com.chengym.model.WXSessionModel;
import com.chengym.service.UserService;
import com.chengym.utils.encryptrd.AesCbcUtil;
import com.chengym.wx.bean.CheckBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
public class WXMessageController {

    @GetMapping("/user/message")
    public String  tokenCheck(@RequestParam("signature")String signature,@RequestParam("timestamp")String timestamp,@RequestParam("nonce")String nonce,@RequestParam("echostr")String echostr){

        log.info("signature : {}--timestamp：{}--nonce：{}--echostr：{}",signature,timestamp,nonce,echostr);
        return echostr;
    }
}
