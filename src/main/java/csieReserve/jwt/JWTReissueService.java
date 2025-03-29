package csieReserve.jwt;

import csieReserve.Repository.RefreshRepository;
import csieReserve.domain.Refresh;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JWTReissueService {

    private final RefreshRepository refreshRepository;  // 리프레시 토큰을 저장한 Repository

    @Autowired
    public JWTReissueService(RefreshRepository refreshRepository) {
        this.refreshRepository = refreshRepository;
    }
    // 매일 자정에 만료된 리프레시 토큰을 삭제하는 작업

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        List<Refresh> expiredTokens = refreshRepository.findByExpirationBefore(now);
        if (!expiredTokens.isEmpty()) {
            // 만료된 토큰 삭제
            refreshRepository.deleteAll(expiredTokens);
        }
    }

}
