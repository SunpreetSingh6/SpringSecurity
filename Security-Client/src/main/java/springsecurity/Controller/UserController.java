package springsecurity.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import springsecurity.Extras.Events.RegistrationCompleteEvent;
import springsecurity.Extras.PasswordModel;
import springsecurity.Extras.UserDetailsModel;
import springsecurity.Model.PasswordResetToken;
import springsecurity.Model.UserDetails;
import springsecurity.Model.VerificationToken;
import springsecurity.Service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/hello")
    public String helloMsg(){
        return "Hello";
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserDetailsModel userDetailsModel , HttpServletRequest request){
        UserDetails user = userService.registerUser(userDetailsModel);
        // Making an event to send user activation mail to user.
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistrationToken")
    public String validateVerificationToken(@RequestParam("token") String token){
//        System.out.println("verifyRegistrationToken called");
        String msg = userService.validateVerficationToken(token);
        if(msg.equalsIgnoreCase("valid")){
            return "User verifies successfully";
        }
        return "Bad User";
    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String token , HttpServletRequest request){
        VerificationToken verificationToken = userService.resendVerificationToken(token);
        sendVerificationCode(verificationToken,applicationUrl(request));
         return "Verification Link Sent";
    }

    @GetMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel , HttpServletRequest request){
        UserDetails userDetails = userService.findUserByEmail(passwordModel.getEmail());
        String  url = "";
        if(userDetails!=null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenByUser(userDetails,token);
            url = createPasswordResetTokenMail(applicationUrl(request),token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token , @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
            return "Invalid Token";
        }
        UserDetails userDetails = userService.getUserByPasswordResetToken(token);
        userService.changePassword(userDetails,passwordModel.getNewPassword());
        return "Password reset successfully";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        UserDetails userByEmail = userService.findUserByEmail(passwordModel.getEmail());
        if(!userService.ckeckValidOldPassword(userByEmail,passwordModel.getOldPassword())){
            return "Invalid old Password";
        }
        userService.changePassword(userByEmail,passwordModel.getNewPassword());
        return "Password Changed Successfully";
    }

    private String createPasswordResetTokenMail(String applicationUrl, String token) {
        String url = applicationUrl + "/savePassword?token=" + token;
        log.info("Click the link reset your password :- " + url);
        return url;
    }

    private void sendVerificationCode(VerificationToken verificationToken, String applicationUrl) {
        String url = applicationUrl + "/verifyRegistrationToken?token=" + verificationToken.getToken();
        log.info("Click the link to verify your account :- " + url);
//        log.info(url);
    }

    private String applicationUrl(HttpServletRequest request) {
//        System.out.println("http://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath());
        String url = "http://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath();
        return url;
    }

}
