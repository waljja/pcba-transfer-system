package com.ht.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht.entity.UserEntity;
import com.ht.mapper.UserMapper;
import com.ht.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	UserMapper mapper;
	
	@Override
	public int insert(UserEntity user) {
		// TODO Auto-generated method stub
		return mapper.InsertUser(user);
	}

	@Override
	public int update(UserEntity user) {
		// TODO Auto-generated method stub
		return mapper.UpdateUser(user);
	}

	@Override
	public int delete(String id) {
		// TODO Auto-generated method stub
		return mapper.DeleteUser(id);
	}
	
	@Override
	public UserEntity SelAccount(String account,String password) {
		// TODO Auto-generated method stub
		return mapper.SelAccount(account,password);
	}

	

}
