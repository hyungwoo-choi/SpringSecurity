package csieReserve.jwt;

import csieReserve.domain.User;
import csieReserve.domain.UserRole;
import csieReserve.dto.security.CustomUserDetails;
import csieReserve.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.lettuce.core.models.role.RedisInstance;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
// 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");

// 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            System.out.println("authorization token is null" );
            filterChain.doFilter(request, response);
            return;
        }

// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            System.out.println("authorization token expired");
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("authorization token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

// 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("authorization")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid authorization token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

// username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);
        Long userId = jwtUtil.getUserId(accessToken);

        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setStudentId(username);
        userEntity.setUserRole(UserRole.valueOf(role));
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
