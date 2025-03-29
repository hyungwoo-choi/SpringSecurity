package csieReserve.Repository;

import csieReserve.domain.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    Boolean existsByRefreshToken(String refreshToken);

    @Transactional
    void deleteByRefreshToken(String refreshToken);

    List<Refresh> findByExpirationBefore(long currentTime);
}
