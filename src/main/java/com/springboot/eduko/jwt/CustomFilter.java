package com.springboot.eduko.jwt;

import com.springboot.eduko.dtos.BaseUserDto;
import com.springboot.eduko.service.TokenBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class CustomFilter extends OncePerRequestFilter {

    private final HandleToken handleToken;
    private final TokenBlackListService tokenBlackListService;

    @Autowired
    public CustomFilter(HandleToken handleToken,TokenBlackListService tokenBlackListService) {
        this.handleToken = handleToken;
        this.tokenBlackListService=tokenBlackListService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                   String token=request.getHeader("Authorization");
                   if (Objects.isNull(token) || !token.startsWith("Bearer ")) {
                       response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token.not.valid");
                       throw new RuntimeException("Token.not.valid");
                   }
                   token=token.substring(7);
                   if(tokenBlackListService.isBlackList(token)){
                       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                       response.setContentType("application/json");
                       response.setCharacterEncoding("UTF-8");

                       response.getWriter().write("""
                                 {
                                    "messageEn": "token logout",
                                    "messageAr": "التكون حصل له خروج"
                                      }
                                 """);

                       return;
                            }
                   BaseUserDto baseUserDto=handleToken.validateToken(token);
                   if(Objects.isNull(baseUserDto)) {
                       response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token.not.valid");
                       throw new RuntimeException("Token.not.valid");
                   }
        List<SimpleGrantedAuthority> roles=baseUserDto.getRoles().stream().map(role->new SimpleGrantedAuthority("ROLE_"+role.getRole())).toList();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(baseUserDto.getEmail(),baseUserDto.getPassword(),roles));
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("/login") || request.getRequestURI().contains("/signup")|| request.getRequestURI().contains("/resetPass")||
                request.getRequestURI().contains("/v3/api-docs")|| request.getRequestURI().contains("/swagger-ui");
    }
}
