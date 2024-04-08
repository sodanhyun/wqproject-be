package com.codehows.wqproject.controller;

import com.codehows.wqproject.dto.LoginDto;
import com.codehows.wqproject.dto.MemberDto;
import com.codehows.wqproject.dto.TokenDto;
import com.codehows.wqproject.entity.Member;
import com.codehows.wqproject.jwt.JwtFilter;
import com.codehows.wqproject.service.AuthService;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
//@RequestMapping("/auth")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid MemberDto memberDto) {
        try{
            authService.signup(memberDto);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }

    }

    @GetMapping("/tempToken")
    public ResponseEntity<?> tempToken() {
        try{
            HashMap<String, Object> result = authService.tempSignin();
            TokenDto tokenDto = (TokenDto)(result.get("tokenDto"));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER,
                    "Bearer " + tokenDto.getAccessToken());
            return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authorize(@Valid LoginDto loginDto) {
        try{
            HashMap<String, Object> result = authService.signin(loginDto);
            TokenDto tokenDto = (TokenDto)(result.get("tokenDto"));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER,
                    "Bearer " + tokenDto.getAccessToken());
            return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>("아이디 혹은 비밀번호가 틀렸습니다.", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // 리프레시 토큰을 저장하는 쿠키를 제거합니다.
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0); // 쿠키 만료시간을 0으로 설정하여 삭제
        refreshTokenCookie.setPath("/logout"); // 쿠키의 경로 설정 (로그아웃 처리와 같은 경로로 설정)
        response.addCookie(refreshTokenCookie); // 응답 헤더에 쿠키 추가
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenDto tokenDto) {
        log.info("refresh in");
        try{
            return ResponseEntity.ok().body(authService.refresh(tokenDto));
        }catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

}