package com.bhs.springsecuritydemo.configs;

import com.bhs.springsecuritydemo.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final RequestMatcher[] urlMatchers = new RequestMatcher[]
            {new AntPathRequestMatcher("/api/v1/auth/validate", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/api/v1/users/me",HttpMethod.GET.name()),
            new AntPathRequestMatcher("/api/v1/users",HttpMethod.GET.name())};

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        for(RequestMatcher urlMatcher : urlMatchers){
            RequestMatcher matcher = new NegatedRequestMatcher(urlMatcher);
            if(!matcher.matches(request)){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if(request.getCookies() == null){
            filterChain.doFilter(request,response);
            return;
        }

        try{
            String jwtToken = null;
            for(Cookie cookie : request.getCookies()){
                    if(cookie.getName().equals("JwtToken")){
                        jwtToken = cookie.getValue();
                        break;
                    }
            }

            final String email = jwtService.extractUsername(jwtToken);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(email != null && authentication == null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if(jwtService.isTokenValid(jwtToken, userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(),
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request,response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request,response,null,exception);
        }
    }
}
