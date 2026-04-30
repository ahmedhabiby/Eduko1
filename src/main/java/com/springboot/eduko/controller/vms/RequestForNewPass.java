package com.springboot.eduko.controller.vms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RequestForNewPass {
    private String email;
    private String newPassword;
}
