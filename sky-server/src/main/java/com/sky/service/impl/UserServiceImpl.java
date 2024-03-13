package com.sky.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.UserNotLoginException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import io.swagger.util.Json;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper mapper;

    @Autowired
    WeChatProperties weChat;

    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {

        String openid = getOpenid(userLoginDTO);
        if(openid==null){
            throw  new UserNotLoginException(MessageConstant.LOGIN_FAILED);
        }

        User user = mapper.getOpenId(openid);

        if(user==null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
        mapper.insert(user);
        }

        return user;
    }

    private String getOpenid(UserLoginDTO userLoginDTO) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("appid",weChat.getAppid());
        map.put("secret",weChat.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type","authorization_code");

        String s = HttpClientUtil.doGet("https://api.weixin.qq.com/sns/jscode2session", map);
        JSONObject jsonObject = JSON.parseObject(s);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
