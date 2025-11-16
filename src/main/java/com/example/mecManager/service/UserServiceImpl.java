package com.example.mecManager.service;

import com.example.mecManager.Common.AppConstants;
import com.example.mecManager.auth.JwtUtils;
import com.example.mecManager.model.*;
import com.example.mecManager.repository.DocInfoRepository;
import com.example.mecManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DocInfoRepository docInfoRepository;
    private final JwtUtils jwtUtils;


    @Override
    public ResponseObject register(User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if(userOptional.isPresent()) {
            DocInfo docInfo = docInfoRepository.findByUserId(userOptional.get().getId());
        }

        if(userOptional.isPresent()) {
            return new ResponseObject(AppConstants.STATUS.ALREADY_EXISTS, "Tài khoản đã tồn tại", null);
        }
        user.setIsActive(AppConstants.STATUS.ACTIVE);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setProfilePictureUrl(AppConstants.URL.IMG_URL);
        user.setGender(AppConstants.GENDER.OTHER);
        User user1 = userRepository.save(user);
        return new ResponseObject(AppConstants.STATUS.SUCCESS, "Đăng ký thành công!", user1);
    }

    @Override
    public ResponseObject login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new ResponseObject(AppConstants.STATUS.NOT_FOUND, "Tài khoản không tồn tại", null);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return new ResponseObject(AppConstants.STATUS.UNAUTHORIZED, "Mật khẩu không đúng", null);
        }

        if (Objects.equals(user.getIsActive(), AppConstants.STATUS.INACTIVE)) { // nếu có trường trạng thái
            return new ResponseObject(AppConstants.STATUS.ERROR, "Tài khoản đã bị khóa", null);
        }

        String token = jwtUtils.generateToken(user);
        return new ResponseObject(AppConstants.STATUS.SUCCESS, "Đăng nhập thành công", token);
    }

    @Override
    public ResponseObject getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            return new ResponseObject(AppConstants.STATUS.SUCCESS, "Truy xuất user có id: " + user.getId() + " thành công.", user);
        }else{
            return new ResponseObject(AppConstants.STATUS.NOT_FOUND, " User không tồn tại ", null);
        }
    }

}
