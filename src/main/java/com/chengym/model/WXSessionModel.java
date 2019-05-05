package com.chengym.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WXSessionModel {

	private String session_key;
	private String openid;
	private String unionid;
}
