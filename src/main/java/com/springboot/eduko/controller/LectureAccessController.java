package com.springboot.eduko.controller;

import com.springboot.eduko.controller.vms.AccessRequest;
import com.springboot.eduko.controller.vms.EnrollResponse;
import com.springboot.eduko.dtos.LectureAccessDto;
import com.springboot.eduko.service.LectureAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class LectureAccessController {
    private final LectureAccessService lectureAccessService;

    @Autowired
    public LectureAccessController(LectureAccessService lectureAccessService) {
        this.lectureAccessService = lectureAccessService;
    }

    @PostMapping("/grantAccess")
    public ResponseEntity<EnrollResponse> grantAccess(@RequestBody AccessRequest accessRequest) throws URISyntaxException {
        return ResponseEntity.created(new URI("/grantAccess")).body(lectureAccessService.giveAccess(accessRequest));
    }
}
