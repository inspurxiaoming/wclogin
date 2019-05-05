package com.chengym.bean;

import lombok.Data;

/**
 * desc:
 *
 * @param
 * @author chengym
 * @return
 * @date 2018/12/02 22:27
 */
@Data
public class UserInfo {
    private String nickName;
    private String gender;
    private String language;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
}
