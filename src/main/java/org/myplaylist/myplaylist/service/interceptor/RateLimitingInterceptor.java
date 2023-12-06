package org.myplaylist.myplaylist.service.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    public RateLimitingInterceptor(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String clientIp = request.getRemoteAddr();

        if (!rateLimitingService.isAllowed(clientIp)) {
            response.setStatus(429);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("Too many requests. Please try again later.");
            return false;
        }

        return true;
    }
}
