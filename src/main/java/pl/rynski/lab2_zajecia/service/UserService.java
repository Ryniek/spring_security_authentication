package pl.rynski.lab2_zajecia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.rynski.lab2_zajecia.model.AppUser;
import pl.rynski.lab2_zajecia.model.AppUserRole;
import pl.rynski.lab2_zajecia.model.VerificationToken;
import pl.rynski.lab2_zajecia.repository.AppUserRepository;
import pl.rynski.lab2_zajecia.repository.AppUserRoleRepository;
import pl.rynski.lab2_zajecia.repository.VerificationTokenRepository;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private AppUserRepository appUserRepository;
    private PasswordEncoder passwordEncoder;
    private VerificationTokenRepository verificationTokenRepository;
    private MailSenderService mailSenderService;
    private AppUserRoleRepository appUserRoleRepository;

    @Autowired
    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, MailSenderService mailSenderService, AppUserRoleRepository appUserRoleRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailSenderService = mailSenderService;
        this.appUserRoleRepository = appUserRoleRepository;
    }

    public void addNewUser(AppUser user, HttpServletRequest request) throws MessagingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        appUserRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);

        String url = "http://" + request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath() +
                "/verify-token?token=" + token;

        mailSenderService.sendMail(user.getUsername(),
                "VerificationToken",
                url,
                false);
    }

    public void verifyToken(String token) {
        AppUser user = verificationTokenRepository.findByValue(token).getAppUser();
        user.setEnabled(true);
        appUserRepository.save(user);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createSuperAdmin() {
        if (appUserRepository.findByUsername("michalrynski96@gmail.com") == null) {
            AppUser admin = new AppUser();
            admin.setUsername("michalrynski96@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));

            AppUserRole appUserRole = new AppUserRole();
            appUserRole.setName("ROLE_ADMIN");
            appUserRole.getUsers().add(admin);
            admin.getRoles().add(appUserRole);

            appUserRoleRepository.save(appUserRole);
            appUserRepository.save(admin);
        }
    }

}
