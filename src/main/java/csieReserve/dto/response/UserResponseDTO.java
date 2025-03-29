package csieReserve.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private String userRole;
    private String studentId;
    private String name;
}
