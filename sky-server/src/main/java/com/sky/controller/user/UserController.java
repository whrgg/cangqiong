package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
public class UserController {

    @Autowired
    UserService userservice;
    @Autowired
    JwtProperties jwt;


    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){

        User user = userservice.wxlogin(userLoginDTO);

        Map<String, Object> claims=new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());

        String token = JwtUtil.createJWT(this.jwt.getUserSecretKey(), this.jwt.getUserTtl(), claims);


        UserLoginVO uvo = UserLoginVO.builder()
                .openid(user.getOpenid())
                .id(user.getId())
                .token(token)
                .build();
        return Result.success(uvo);
    }
}
