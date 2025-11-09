package com.example.mecManager.controller;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.model.ResponseObject;
import com.example.mecManager.model.User;
import com.example.mecManager.model.UserDTO;
import com.example.mecManager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.URL.API_URL+"/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<ResponseObject> registerUser(@RequestBody User user) {
        ResponseObject responseObject = userService.register(user);
        if(responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)){
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> loginUser(@RequestBody UserDTO loginRequest) {
        ResponseObject responseObject = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if(responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
        }
    }

    @GetMapping("/get-user-by-id")
    public ResponseEntity<ResponseObject> getUserById(@RequestBody Long userId) {
        ResponseObject responseObject = userService.getUserById(userId);
        if(responseObject.getStatus().equals(AppConstants.STATUS.SUCCESS)) {
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
        }
    }

}
