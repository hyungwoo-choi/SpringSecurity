package csieReserve.service;

import csieReserve.Repository.UserRepository;
import csieReserve.domain.User;
import csieReserve.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipal;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;


//   파라미터가 db의 이름, html태그하고 같아야 됨.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userData = userRepository.findByStudentId(username);
        if(userData != null){
            return new CustomUserDetails(userData);
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
