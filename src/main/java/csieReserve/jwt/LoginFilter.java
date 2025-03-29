package csieReserve.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import csieReserve.Repository.RefreshRepository;
import csieReserve.domain.Refresh;
import csieReserve.dto.request.LoginRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.dto.response.UserResponseDTO;
import csieReserve.dto.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;


    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        // 필터가 적용될 URL을 "/api/login"으로 설정
        setFilterProcessesUrl("/api/login123");
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDTO loginRequestDTO = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getStudentId(), loginRequestDTO.getPassword()));
        }
        catch (IOException e) {
            System.out.println("LoginFilter: Error while parsing login request");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        String username = authentication.getName();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        String access = jwtUtil.createJwt("authorization",principal.getId(), username, role, 60*60*10*1000L);
        String refresh = jwtUtil.createJwt("refresh",principal.getId(), username, role, 86400000*1000L);

        addRefreshToken(username, refresh, 86400000L);

        response.setHeader("authorization", access);
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh", refresh)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        response.setStatus(HttpStatus.OK.value());

        // ✅ JSON 응답 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .userRole(role)
                .studentId(principal.getStudentId())
                .name(username)
                .build();

        ApiResponse<UserResponseDTO> apiResponse = new ApiResponse<>("로그인 성공", responseDTO);
        String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiResponse<?> apiResponse = new ApiResponse<>("아이디 또는 비밀번호가 틀렸습니다.", null);
        ResponseEntity<ApiResponse<?>> responseEntity = ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                .body(apiResponse);

        // 응답을 클라이언트로 반환
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // 403 상태 코드 설정
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
        response.getWriter().flush();
    }

    private void addRefreshToken(String username, String refreshToken, Long expirationTime){
        Date date = new Date(System.currentTimeMillis() + expirationTime);
        Refresh refresh = new Refresh();
        refresh.setUsername(username);
        refresh.setRefreshToken(refreshToken);
        refresh.setExpiration(date.getTime());

        refreshRepository.save(refresh);
    }

}
