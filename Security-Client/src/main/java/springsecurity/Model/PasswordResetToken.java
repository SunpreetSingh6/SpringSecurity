package springsecurity.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {

    private final int EXPIRE_TIME=10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false , foreignKey = @ForeignKey(name = "FK_USER_RESETPASS_TOKEN"))
    private UserDetails userDetails;

    public PasswordResetToken(UserDetails userDetails,String token){
        super();
        this.userDetails = userDetails;
        this.token = token;
        this.expirationTime = calculateExpirationDate(EXPIRE_TIME);
    }
    public PasswordResetToken(String token){
        super();
        this.token = token;
    }

    private Date calculateExpirationDate(int expireTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE,expireTime);
        return new Date(calendar.getTime().getTime());
    }


}
