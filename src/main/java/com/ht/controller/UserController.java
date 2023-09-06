package com.ht.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.reflect.Field;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht.api.CommonResult;
import com.ht.api.ResultCode;
import com.ht.entity.UserEntity;
import com.ht.service.UserService;

/**
 * 登录接口
 *
 * @author 张越
 */
@Api("JavaAPI接口")
@CrossOrigin
@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Autowired
    public UserService userService;

    /**
     * 用户登录
     *
     * @param user 账号
     * @return 登录结果
     */
    @ApiOperation(value = "登入账号")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<String> SelAccount(@RequestBody UserEntity user) {
        ResultCode result;
        String[] strArray;
        UserEntity state = userService.SelAccount(user.getAccount(), user.getPassword());
        if (checkObjFieldIsNotNull(state)) {
            strArray = state.getPermissions().split(",");
        } else {
            result = ResultCode.LOGINGFAILED;
            return CommonResult.failed(result);
        }
        return CommonResult.successLogin("登入成功", state.getFactory(), state.getNode(), state.getName(), strArray);
    }

    public static boolean checkObjFieldIsNotNull(Object obj) {
        boolean flag = false;
        try {
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(obj) == null || f.get(obj) == "") {
                } else {
                    flag = true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return flag;
    }

}
