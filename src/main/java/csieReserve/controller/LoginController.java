package csieReserve.controller;

import csieReserve.dto.request.LoginRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.dto.response.UserResponseDTO;
import csieReserve.dto.security.CustomUserDetails;
import csieReserve.jwt.JWTReissueService;
import csieReserve.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {
    @Operation(summary = "로그인 API")
    @PostMapping("/login123")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequestDTO requestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .userRole(userDetails.getUserRole())
                .studentId(userDetails.getStudentId())
                .name(userDetails.getUsername())
                .build();

        return ResponseEntity.ok(new ApiResponse<>("로그인 성공", responseDTO));
    }

    @Operation(summary = "비밀번호 재설정 API")
    @PostMapping("/password/modify")
    public ResponseEntity<ApiResponse<?>> modifyPassword(){
        return ResponseEntity.ok(new ApiResponse<>("sadf",null));
    }

}


