package com.springboot.eduko.service;

import com.springboot.eduko.controller.vms.LectureRequest;
import com.springboot.eduko.dtos.LectureDto;

public interface LectureService {
    LectureDto saveLecture(LectureRequest lectureRequest);
}
