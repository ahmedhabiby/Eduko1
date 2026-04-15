package com.springboot.eduko.jwt;

import com.springboot.eduko.dtos.BaseUserDto;
import com.springboot.eduko.dtos.StudentDto;
import com.springboot.eduko.service.BaseUserService;
import com.springboot.eduko.service.StudentService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Configuration
public class HandleToken {
    private  Token token;
    private BaseUserService baseUserService;
    private JwtBuilder jwtBuilder;
    private JwtParser jwtParser;
    private Duration duration;


    HandleToken(Token token, BaseUserService baseUserService) {
        this.token = token;
        this.baseUserService = baseUserService;
       Key key= Keys.hmacShaKeyFor(token.getSecret().getBytes());
       jwtBuilder= Jwts.builder().signWith(key);
       jwtParser= Jwts.parser().setSigningKey(key);
       duration=token.getDuration();
    }

    public String generateToken(BaseUserDto baseUserDto){
        return jwtBuilder
                .setSubject(baseUserDto.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(duration)))
                .claim("roles",baseUserDto.getRoles().stream().map(role->new SimpleGrantedAuthority("Role_"+role.getRole())).toList())
                .compact();
    }
    public BaseUserDto validateToken(String token){
        if(!jwtParser.isSigned(token))
            throw new RuntimeException("Token.not.valid");
        Claims claims =jwtParser.parseClaimsJws(token).getBody();
        String email=claims.getSubject();
        Date expiration=claims.getExpiration();
        Date issuedAt=claims.getIssuedAt();
        BaseUserDto baseUserDto=baseUserService.getUserByEmail(email);
        boolean isExpired=(Objects.isNull(baseUserDto)) && issuedAt.before(expiration) && expiration.after(new Date());
        if(isExpired)
            throw new RuntimeException("Token.expired");
        return baseUserDto;
    }
    public Date getExpireDateFromToken(String token){
        Claims claims =jwtParser.parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }
}
