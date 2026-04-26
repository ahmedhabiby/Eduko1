package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.LectureRequest;
import com.springboot.eduko.dtos.LectureDto;
import com.springboot.eduko.service.LectureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class LectureController {
    private final LectureService lectureService;

    @Autowired
    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @PostMapping("/saveLecture")
    public ResponseEntity<LectureDto> saveLecture(@RequestBody @Valid LectureRequest lectureRequest) throws URISyntaxException {
        return ResponseEntity.created(new URI("/saveLecture")).body(lectureService.saveLecture(lectureRequest));
    }
}
