package pl.lodz.p.it.ssbd2023.ssbd05.utils;


import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {
    public static String getIpAddress(HttpServletRequest request) {
        return request.getHeader("X-Real-IP");
    }
}
