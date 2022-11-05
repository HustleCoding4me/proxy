package hello.proxy.pureproxy.proxy.code;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ProxyPatternTest {


    /**
     * 실제 객체에서 변하지 않는 데이터를 계속 받는 상황.
     * 프록시 패턴으로 Cache를 사용하여 접근을 제어해서, 이미 받은 데이터는 캐싱을 하자.
     * 실제 객체의 데이터를 받는 시간이 1초라고 가정하자.
     */
    @Test
    void noProxyTest() {
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        client.execute();
        client.execute();
        client.execute();
    }


    /**
     * 프록시 패턴을 사용해서, 이미 받은 데이터는 캐싱을 하자.
     * 처음에는 CacheProxy가 RealSubject를 호출하고,
     * 그 다음부터는 CacheProxy에 저장된 data를 호출한다.
     * 그래서 1초만에 끝난다.(3초가 아닌)
     */
    @Test
    void cacheProxyTest() {
        RealSubject realSubject = new RealSubject();
        CacheProxy cacheProxy = new CacheProxy(realSubject);
        ProxyPatternClient proxyPatternClient = new ProxyPatternClient(cacheProxy);

        proxyPatternClient.execute();
        proxyPatternClient.execute();
        proxyPatternClient.execute();
    }

}
