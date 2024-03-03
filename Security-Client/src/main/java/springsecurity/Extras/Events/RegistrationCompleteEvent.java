package springsecurity.Extras.Events;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import springsecurity.Model.UserDetails;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private UserDetails userDetails;
    private String url;

    public RegistrationCompleteEvent(UserDetails user, String url) {
        super(user);
        this.userDetails = user;
        this.url = url;
    }

}
