package springsecurity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.Model.UserDetails;

@Repository
public interface UserRepository extends JpaRepository<UserDetails,Long> {
    UserDetails findByEmail(String email);
}
