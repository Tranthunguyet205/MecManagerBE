package com.example.mecManager.service;

import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.model.User;

public interface UserService {
    ResponseObject register(User user);
    ResponseObject login(String username, String password);

}
