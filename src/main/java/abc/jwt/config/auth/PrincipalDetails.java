package abc.jwt.config.auth;


import abc.jwt.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private User user;
    public PrincipalDetails(User user){
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    //
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    //로그인에선, 토큰의 두번째 파라메터(credential)와 해당함수 비교로 판단.

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        user.getRoleList().forEach(r -> {
            authorities.add(() -> {return r;});
        });
        return authorities;
    }
}
