package com.springboot.eduko.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "token")
public class Token {
    private String secret;
    private Duration duration;
}
