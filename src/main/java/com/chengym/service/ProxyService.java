package com.chengym.service;

import com.chengym.common.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
public class ProxyService {
    @Autowired
    RestTemplate restTemplate;
    @Value("${proxyUrl.customerUrl}")
    String customerUrl;
    @Value("${proxyUrl.tradeUrl}")
    String tradeUrl;
    @Value("${proxyUrl.resourceUrl}")
    String resourceUrl;
    @Value("${proxyUrl.siteUrl}")
    String siteUrl;

    public ResponseBean getCustomer(){
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(customerUrl,ResponseBean.class,"");
        return responseEntity.getBody();
    }
    public ResponseBean getTrade(){
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(tradeUrl,ResponseBean.class,"");
        return responseEntity.getBody();
    }

    public ResponseBean getResource() {
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(resourceUrl,ResponseBean.class,"");
        return responseEntity.getBody();
    }

    public ResponseBean getSite() {
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(siteUrl,ResponseBean.class,"");
        return responseEntity.getBody();
    }
}
