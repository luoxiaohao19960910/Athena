package com.athena.util.filter;

import com.athena.exception.internal.JwtAuthException;
import com.athena.service.security.TokenAuthenticationService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by tommy on 2017/3/22.
 */
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenAuthenticationService tokenAuthenticationService;

    /**
     * Instantiates a new Jwt authentication filter.
     *
     * @param tokenAuthenticationService the token authentication service
     */
    public JwtAuthenticationFilter(TokenAuthenticationService tokenAuthenticationService) {
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Authentication authentication = null;
            try {
                authentication = tokenAuthenticationService.getAuthentication((HttpServletRequest) servletRequest);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(servletRequest, servletResponse);
                SecurityContextHolder.getContext().setAuthentication(null);//Clear after request
            } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
                JwtAuthException exception = new JwtAuthException(e);
                SecurityContextHolder.getContext().setAuthentication(null);
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.sendError(exception.getStatusCode(), exception.toString());
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

}
