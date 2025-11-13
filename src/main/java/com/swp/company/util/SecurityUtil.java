package com.swp.company.util;

import com.swp.company.entity.Permission;
import com.swp.company.entity.Role;
import com.swp.company.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    @Value("${access-token-time}")
    private long tokenExpireTime;
    @Value("${refresh_token-time}")
    private long refreshTokenExpireTime;
    @Value("${active_token-time}")
    private long activeTokenExpireTime;

    public String createToken(User u) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.tokenExpireTime, ChronoUnit.SECONDS);
        Role role = u.getRole();
        Set<String> listPermissons = new HashSet<>();
        List<Permission> permissions = role.getPermissions();
        for (Permission permission : permissions) {
            listPermissons.add(permission.getPermission());
        }
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(u.getEmail())
                .claim("data", u.getEmail())
                .claim("permissions", listPermissons)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
    public String createRefreshToken(User u) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpireTime, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(u.getEmail())
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
    public boolean validateToken(String token) {
        try {
            if(token.startsWith("Bearer ")){
                jwtDecoder.decode(token.substring(7));
            }
            else{
                jwtDecoder.decode(token);
            }
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getExpiration(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token.substring(7));
            if (jwt.getExpiresAt() != null) {
                return jwt.getExpiresAt().toEpochMilli();
            } else {
                return null; // Trả về null nếu token không có thời gian hết hạn
            }
        } catch (JwtException e) {
            return null; // Trả về null nếu token không hợp lệ
        }
    }
    public String createActiveAndForgotToken(String email) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.activeTokenExpireTime, ChronoUnit.SECONDS);
//        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        System.out.println("Issued at: " + now);
        System.out.println("Expires at: " + validity);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("data",email)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String getEmailRequest() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    public String getEmailFromToken(String token) {
        try {
            Jwt jwt = null;
            if(token.startsWith("Bearer ")){
                jwt = jwtDecoder.decode(token.substring(7));
            }
            else{
                jwt =  jwtDecoder.decode(token);
            }
            return jwt.getSubject();
        } catch (JwtException e) {
            // Xử lý lỗi giải mã token nếu cần
            System.out.println(">>> JWT error: " + e.getMessage());
            return null; // Hoặc ném ngoại lệ
        }
    }
}
