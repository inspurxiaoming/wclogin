package com.chengym.wx.bean;

import lombok.Data;

/**
 * desc:
 *
 * @param
 * @author chengym
 * @return
 * @date 2018/12/19 17:23
 */
@Data
public class CheckBean {
    private String signature;
    private String timestamp;
    private String nonce;
    private String echostr;
}
