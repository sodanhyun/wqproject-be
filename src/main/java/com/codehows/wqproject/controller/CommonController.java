package com.codehows.wqproject.controller;

import com.codehows.wqproject.dto.LectureDto;
import com.codehows.wqproject.service.LectureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Slf4j
public class CommonController {

    private final LectureService lectureService;
    @GetMapping("/question/limit/{lCode}")
    public ResponseEntity<?> lectureDetail(@PathVariable String lCode) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            LectureDto lectureDto = lectureService.findLecture(lCode);
            result.put("title", lectureDto.getTitle());
            result.put("limitMin", lectureDto.getLimitMin());
            result.put("active", lectureDto.getActive());
            return ResponseEntity.ok().body(result);
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>("해당 강의를 찾을 수 없습니다.", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }
}
