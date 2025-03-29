package csieReserve.Repository;

import csieReserve.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByStudentId(String studentId);
    @Query("select u.name from User u where u.studentId = ?1")
    String findNameByStudentId(String studentId);

    User findByStudentId(String username);

}
