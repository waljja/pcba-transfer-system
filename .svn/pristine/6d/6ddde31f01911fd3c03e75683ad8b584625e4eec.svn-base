package com.ht.mapper;

import org.apache.ibatis.annotations.Param;

import com.ht.entity.UserEntity;

public interface UserMapper {
	
	/**
	 * 添加用户信息
	 * @param user
	 * @return
	 */
	int InsertUser(UserEntity user);
	
	/**
	 * 修改用户信息
	 * @param user
	 * @return
	 */
	int UpdateUser(UserEntity user);
	
	/**
	 * 删除用户信息
	 * @param id
	 * @return
	 */
	int DeleteUser(String id);
	
	/**
	 * 用户登入
	 * @param user
	 * @return
	 */
	UserEntity SelAccount(@Param("account")String account,@Param("password")String password);
	
	
	
	

}
