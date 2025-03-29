package csieReserve.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import csieReserve.dto.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import okhttp3.internal.http2.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try{
            filterChain.doFilter(request, response);

        }
        catch (ExpiredJwtException e){
            //토큰의 유효기간 만료
            setErrorResponse(response, "토큰의 유효기간이 만료하였습니다." , HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (JwtException | IllegalArgumentException e){
            //유효하지 않은 토큰
            setErrorResponse(response, "유효하지 않은 토큰입니다." , HttpServletResponse.SC_BAD_REQUEST);
        }catch (InvalidTokenException e){
            //잘못된 토큰 예외 처리
            setErrorResponse(response, "유효하지 않은 토큰입니다.", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e){
            //기타 에러
            setErrorResponse(response, "기타 에러.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void setErrorResponse(HttpServletResponse response, String message, int code){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiResponse apiResponse = new ApiResponse<>(message, null);
        ResponseEntity<ApiResponse> errorResponse = ResponseEntity.status(code).body(apiResponse);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}





