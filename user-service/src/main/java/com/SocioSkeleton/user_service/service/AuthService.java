package com.SocioSkeleton.user_service.service;

import com.SocioSkeleton.user_service.dto.LoginRequestDto;
import com.SocioSkeleton.user_service.dto.SignUpRequestDto;
import com.SocioSkeleton.user_service.dto.UserDto;
import com.SocioSkeleton.user_service.entity.User;
import com.SocioSkeleton.user_service.exception.BadRequestException;
import com.SocioSkeleton.user_service.exception.ResourceNotFoundException;
import com.SocioSkeleton.user_service.repository.UserRepository;
import com.SocioSkeleton.user_service.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {
        User user = modelMapper.map(signUpRequestDto, User.class);
        user.setPassword(PasswordUtil.hashPassword(signUpRequestDto.getPassword()));

        User saveUser = userRepository.save(user);

        return modelMapper.map(saveUser, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with email: "+ loginRequestDto.getEmail()));

        boolean isPasswordMatch = PasswordUtil.checkPassWord(loginRequestDto.getPassword(), user.getPassword());

        if(!isPasswordMatch) throw new BadRequestException("Incorrect password");
        return jwtService.generateAccessToken(user);
    }
}
