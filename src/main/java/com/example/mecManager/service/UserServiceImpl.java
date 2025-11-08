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
    public ResponseObject register(UserDTO user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if(userOptional.isPresent()) {
            DocInfo docInfo = docInfoRepository.findByUserId(userOptional.get().getId());
        }

        if(userOptional.isPresent() || userOptional2.isPresent()) {
            return new ResponseObject(MessageConstants.FAILED, MessageConstants.THAT_BAI, null);
        }
        user.setIsActive(AppConstants.STATUS.ACTIVE);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(user.getRoleId());
        user.setProfilePictureUrl("");
        user.setPhoneNumber(user.getPhoneNumber());
        user.setProfilePictureUrl("https://i.ibb.co/FL5DXK4f/avatar-trang-4.jpg");
        PriestProfile priestProfile = new PriestProfile();
        User user1 = userRepository.save(user);
        priestProfile.setUser(user1);
        priestProfile.setCreatedAt(new Date());
        priestProfileService.createProfile(priestProfile);
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

}
