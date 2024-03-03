package springsecurity.Service;

import springsecurity.Extras.UserDetailsModel;
import springsecurity.Model.UserDetails;
import springsecurity.Model.VerificationToken;

public interface UserService {
    UserDetails registerUser(UserDetailsModel userDetailsModel);

    void saveVerificationToken(String token, UserDetails userDetails);

    String validateVerficationToken(String token);

    VerificationToken resendVerificationToken(String token);

    UserDetails findUserByEmail(String email);

    void createPasswordResetTokenByUser(UserDetails userDetails, String token);

    String validatePasswordResetToken(String token);

    UserDetails getUserByPasswordResetToken(String token);

    void changePassword(UserDetails userDetails, String newPassword);

    boolean ckeckValidOldPassword(UserDetails userByEmail, String oldPassword);
}
