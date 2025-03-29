package csieReserve.controller;


import csieReserve.dto.request.SignUpRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.service.SignUpService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignUpController {
    private final SignUpService signUpService;
    @Operation(summary = "회원가입 api",
            description = "학번, 이름, 비밀번호값을 통한 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signUp(@RequestBody SignUpRequestDTO requestDTO){
        signUpService.signUp(requestDTO);
        return ResponseEntity.ok(new ApiResponse<>("회원가입 완료",true));
    }
}
