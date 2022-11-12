package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class ReflectionTest {

    /**
     * Reflection이 필요한 상황
     */
    @Test
    void reflection0() {
        Hello target = new Hello();

        /**
         * 호출하는 메서드만 다르고 나머지는 동일한 동작.
         * 공통 로직 1과 2를 하나의 메서드로 뽑아서 합칠 수 있을까?
         */
        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA();//호출하는 메서드가 다름
        log.info("result={}", result1);
        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB();//호출하는 메서드가 다름
        log.info("result={}", result2);
        //공통 로직2 종료

    }

    /**
     * Reflection을 사용.
     * 클래스 정보를 획득하여 사용 예제
     */

    @Test
    void reflection1() throws Exception {
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        //callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        Object result1 = methodCallA.invoke(target);
        log.info("result1={}", result1);

        //callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target);
        log.info("rsult2={}", result2);
    }

    /**
     * Reflection 을 사용해 더 동적으로 만들어보기
     */
    @Test
    void reflection2() throws Exception {
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        //callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);
        //callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
        }

    private void dynamicCall(Method method, Object target) throws Exception{
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}", result);

    }

    /**
     * Runtime에 동작하기 때문에 컴파일 시점에 오류를 잡기 힘들다.
     * 그래서 일반적으로 사용하지 않으면 좋다.
     * 너무 일반적인 공통 처리가 필요할 때 부분적으로 주의해서 사용해야 한다.
     */
    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }

        public String callB() {
            log.info("callB");
            return "B";
        }

    }


}
