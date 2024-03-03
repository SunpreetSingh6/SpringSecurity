package springsecurity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.Model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    VerificationToken findByToken(String token);
}
