package com.springboot.eduko.dtos;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.eduko.model.Lectures;
import com.springboot.eduko.model.Student;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureAccessDto {
    private Long id;
    private int status;
    private Student student;
    private Lectures lectures;
}
