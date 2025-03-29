package csieReserve.dto.request;

import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String name;
    private String userStudnetId;
    private String userPassword;
}
