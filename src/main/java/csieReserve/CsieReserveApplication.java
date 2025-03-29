package csieReserve;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class CsieReserveApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(CsieReserveApplication.class, args);
	}

	/**@PostConstruct
	public void init(){
		// 기본 시간대를 서울로 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	*/
}
