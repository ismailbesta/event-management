package com.example.aksigorta_final.configs; // Paket adını projene göre ayarladım

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class SessionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String urlPath = request.getRequestURI();

        // Giriş yapmadan erişilebilecek (Token/Session gerektirmeyen) endpoint'ler
        String[] freeUrls = {
                "/user/login",
                "/user/register",
        };

        boolean isAuth = true;
        for (String freeUrl : freeUrls) {
            // Eğer istek atılan URL, freeUrls listesindeki bir kelimeyi içeriyorsa filtreyi geç
            if (urlPath.startsWith(freeUrl) || urlPath.contains(freeUrl)) {
                isAuth = false;
                break;
            }
        }

        // İstemci (Client) Bilgilerini Alma
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String method = request.getMethod();
        String query = request.getQueryString();
        String referer = request.getHeader("Referer");

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        HttpSession session = request.getSession(false);
        Object user = (session != null) ? session.getAttribute("user") : null;

        logger.info("""
                
                ====== REQUEST LOG ======
                Time      : {}
                IP        : {}
                Method    : {}
                URL       : {}
                Query     : {}
                Referer   : {}
                UserAgent : {}
                Session   : {}
                User      : {}
                ==========================
                """,
                time, ipAddress, method, urlPath, query, referer, userAgent,
                (session != null ? session.getId() : "No Session"),
                (user != null ? user : "Anonymous")
        );

        if (isAuth) {
            if (user == null) {
                logger.warn("Unauthorized access -> IP: {}, URL: {}", ipAddress, urlPath);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Code
                String jsonResponse = """
                        {
                          "success": false,
                          "message": "Unauthorized access. Please log in."
                        }
                        """;
                response.getWriter().write(jsonResponse);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}