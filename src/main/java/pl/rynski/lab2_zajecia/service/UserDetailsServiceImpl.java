package pl.rynski.lab2_zajecia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.rynski.lab2_zajecia.model.AppUser;
import pl.rynski.lab2_zajecia.model.AppUserRole;
import pl.rynski.lab2_zajecia.repository.AppUserRepository;

import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private AppUserRepository appUserRepository;

    @Autowired
    public UserDetailsServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);
        if(appUser == null) {
            throw new UsernameNotFoundException("Brak usera o takim loginie: " + username);
        }
        User user = new User(appUser.getUsername(), appUser.getPassword(), getAuthorities(appUser.getRoles()));
        System.out.println(user.isEnabled());
        return user;
    }

    private List<GrantedAuthority> getAuthorities(List<AppUserRole> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return authorities;
    }
}
