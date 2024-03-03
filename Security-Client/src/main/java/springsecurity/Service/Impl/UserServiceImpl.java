package springsecurity.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springsecurity.Extras.UserDetailsModel;
import springsecurity.Model.PasswordResetToken;
import springsecurity.Model.UserDetails;
import springsecurity.Model.VerificationToken;
import springsecurity.Repository.PasswordResetTokenRepository;
import springsecurity.Repository.UserRepository;
import springsecurity.Repository.VerificationTokenRepository;
import springsecurity.Service.UserService;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public UserDetails registerUser(UserDetailsModel userDetailsModel) {
        UserDetails userDetails = new UserDetails();
        userDetails.setRole("User");
        userDetails.setEmail(userDetailsModel.getEmail());
        userDetails.setFirstName(userDetailsModel.getFirstName());
        userDetails.setLastName(userDetailsModel.getLastName());
        userDetails.setPassword(passwordEncoder.encode(userDetailsModel.getPassword()) );
        userRepository.save(userDetails);
        return userDetails;
    }

    @Override
    public void saveVerificationToken(String token, UserDetails userDetails) {
        VerificationToken verificationToken = new VerificationToken(userDetails,token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    @Transactional
    public String validateVerficationToken(String token) {

        VerificationToken byToken = verificationTokenRepository.findByToken(token);
        if(byToken == null){
            return "invalid";
        }

        UserDetails userDetails = byToken.getUserDetails();
        Calendar c = Calendar.getInstance();
        if(byToken.getExpirationTime().getTime() - c.getTime().getTime() <= 0){
            verificationTokenRepository.delete(byToken);
            return "expired";
        }

        userDetails.setEnabled(true);
        userRepository.save(userDetails);
        return "valid";
    }

    @Override
    public VerificationToken resendVerificationToken(String token) {
        VerificationToken byToken = verificationTokenRepository.findByToken(token);
        byToken.setToken(UUID.randomUUID().toString());
        return verificationTokenRepository.save(byToken);
    }

    @Override
    public UserDetails findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenByUser(UserDetails userDetails, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(userDetails,token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken byToken = passwordResetTokenRepository.findByToken(token);
        if(byToken==null){
            return "invalid";
        }
        UserDetails userDetails = byToken.getUserDetails();
        Calendar c = Calendar.getInstance();
        if(byToken.getExpirationTime().getTime() - c.getTime().getTime() <= 0){
            passwordResetTokenRepository.delete(byToken);
            return "expired";
        }
        return "valid";
    }

    @Override
    public UserDetails getUserByPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token).getUserDetails();
    }

    @Override
    public void changePassword(UserDetails userDetails, String newPassword) {
        userDetails.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userDetails);
    }

    @Override
    public boolean ckeckValidOldPassword(UserDetails userByEmail, String oldPassword) {
        return passwordEncoder.matches(oldPassword,userByEmail.getPassword());
    }
}
