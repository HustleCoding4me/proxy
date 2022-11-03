package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

//@Import(AppV1Config.class)
@Import({AppV1Config.class,AppV2Config.class})
//클래스를 스프링 빈으로 등록한다. Configuration을 스프링으로 등록한다.
// 아무 스프링 빈으로 하나 등록할 때도 사용한다.
@SpringBootApplication(scanBasePackages = "hello.proxy.app") //주의
//별도 경로 지정을 통해 제한된 @ComponentScan을 사용해 분리한다.
//config 경로는 컴포넌트 스캔 X
public class ProxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}
