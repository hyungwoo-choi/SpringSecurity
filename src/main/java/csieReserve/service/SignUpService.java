package csieReserve.service;

import csieReserve.Repository.UserRepository;
import csieReserve.domain.User;
import csieReserve.dto.request.SignUpRequestDTO;
import csieReserve.exception.InvalidPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static csieReserve.domain.UserRole.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signUp(SignUpRequestDTO signUpRequestDTO){
        User user = new User();
        String password = signUpRequestDTO.getUserPassword();
        user.setName(signUpRequestDTO.getName());
        user.setStudentId(signUpRequestDTO.getUserStudnetId());
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setUserRole(ROLE_ADMIN);
        userRepository.save(user);
    }
}

