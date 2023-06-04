package net.auzy.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomRequestFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {

        /*
         * DO custom request manipulation here if needed as we have wrapper request
         */

        filterChain.doFilter(request, response);

    }
}
