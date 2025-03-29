package csieReserve.service;
import csieReserve.Repository.UserRepository;
import csieReserve.dto.response.UserResponseDTO;
import csieReserve.dto.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO getUserDTOBySession(){
        String studentId = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        return new UserResponseDTO(role, studentId, userRepository.findByStudentId(studentId).getName());
    }
    @Transactional
    public UserResponseDTO getUserDTO(CustomUserDetails userDetails){
        String studentId = userDetails.getStudentId();
        String name = userRepository.findNameByStudentId(studentId);
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        return new UserResponseDTO(role, studentId, name);
    }
}
