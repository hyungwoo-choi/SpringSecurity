package csieReserve.jwt;


import csieReserve.Repository.RefreshRepository;
import csieReserve.domain.Refresh;
import csieReserve.dto.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class JWTReissueController {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;



    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request , HttpServletResponse response){
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for(Cookie cookie : cookies){
            if(cookie.getName().equals("refresh")){
                refresh = cookie.getValue();
            }
        }

        if(refresh == null){
            return ResponseEntity.badRequest().body(new ApiResponse<>("refresh token이 없습니다.",null));
        }

        try {
            jwtUtil.isExpired(refresh);
        }catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("refresh token이 만료되었습니다.",null));
        }


        String category = jwtUtil.getCategory(refresh);


        if(!category.equals("refresh")){
            return ResponseEntity.badRequest().body(new ApiResponse<>("refresh token이 아닙니다.",null));
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("refresh token이 유효하지 않습니다.",null));
        }


        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        Long userId = jwtUtil.getUserId(refresh);

        String newToken = jwtUtil.createJwt("authorization",userId, username, role, 60*60*1*1000L);
        String newRefresh = jwtUtil.createJwt("refresh",userId, username, role, 86400000*1000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefreshToken(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);

        response.setHeader("Authorization", newToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh", newRefresh)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")  // SameSite 설정
                .maxAge(86400)  // 1 day (24 hours)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(new ApiResponse<>("토큰 재발급이 완료되었습니다.",null));
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUsername(username);
        refreshEntity.setRefreshToken(refresh);
        refreshEntity.setExpiration(date.getTime());

        refreshRepository.save(refreshEntity);
    }

//    private Cookie createCookie(String name, String value) {
//        Cookie cookie = new Cookie(name, value);
//        cookie.setMaxAge(24*60*60);
////        cookie.setHttpOnly(true);
////        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//
//        return cookie;
//    }

}
