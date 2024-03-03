package springsecurity.Extras.Events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import springsecurity.Model.UserDetails;
import springsecurity.Service.UserService;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
//        Creating verification token for the user with link
//        Then sending this link with verification token on mail.

        UserDetails userDetails = event.getUserDetails();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationToken(token,userDetails);

        String url = event.getUrl() + "/verifyRegistrationToken?token=" + token;

        log.info("Click on the link to verify your account :-" + url);

    }
}
