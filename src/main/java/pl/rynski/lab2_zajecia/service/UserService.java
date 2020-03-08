package pl.rynski.lab2_zajecia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
import java.util.HashSet;
import java.util.Set;
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

    public void addNewAdmin(AppUser user, HttpServletRequest request) throws MessagingException {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);

        String url = "http://" + request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath() +
                "/verify-admin?token=" + token;

        mailSenderService.sendMail("michalrynski96@gmail.com",
                "Admin request: " + user.getUsername(),
                url,
                false);
    }

    public void addNewUser(AppUser user, HttpServletRequest request) throws MessagingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        setRole(user, "ROLE_USER");

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

    private void setRole(AppUser user, String role) {
        AppUserRole userRole = appUserRoleRepository.findByName(role);
        Set<AppUserRole> roles = new HashSet();
        if(userRole != null) {
            roles.add(userRole);
            user.setRoles(roles);
        } else {
            userRole = new AppUserRole();
            userRole.setName(role);
            userRole.getUsers().add(user);
            roles.add(userRole);
            user.setRoles(roles);
            appUserRoleRepository.save(userRole);
        }
    }

    public void verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByValue(token);
        AppUser user = verificationToken.getAppUser();
        user.setEnabled(true);
        appUserRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    public void verifyAdmin(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByValue(token);
        AppUser user = verificationToken.getAppUser();
        setRole(user, "ROLE_ADMIN");
        appUserRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createSuperAdmin() {
        if (appUserRepository.findByUsername("michalrynski96@gmail.com") == null) {
            AppUser admin = new AppUser();
            admin.setUsername("michalrynski96@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);

            setRole(admin, "ROLE_ADMIN");

            appUserRepository.save(admin);
        }
    }

}
