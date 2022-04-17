package abc.jwt.config.auth;

import abc.jwt.model.User;
import abc.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//로그인 요청 올 시, 해당 정보 db에 있는 사용자인지 확인하는 동작
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        return new PrincipalDetails(user); //객체 타입에 맞게 변환 후, 반환.
    }
}