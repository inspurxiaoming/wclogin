package com.chengym.bean;

import lombok.Data;

/**
 * desc:
 *
 * @param
 * @author chengym
 * @return
 * @date 2018/12/02 22:23
 */
@Data
public class UserLogin {
    private String code;
    private String encryptedData;
    private String iv;
    private UserInfo userInfo;
    private String rawData;
    private String userId;
}
