package hello.proxy.config.v2_dynamicProxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v2_dynamicProxy.handler.LogTraceFilterHandler;
import hello.proxy.config.v2_dynamicProxy.handler.LogTraceHandler;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
public class DynamicProxyFilterConfig {

    /**
     *메서드 명 패턴이 아래와 같아야 handler 실행
     */

    /**
     * [dc4b7a40] interface hello.proxy.app.v1.OrderControllerV1.request()
     * [dc4b7a40] |-->interface hello.proxy.app.v1.OrderServiceV1.orderItem()
     * [dc4b7a40] |   |-->interface hello.proxy.app.v1.OrderRepositoryV1.save()
     * [dc4b7a40] |   |<--interface hello.proxy.app.v1.OrderRepositoryV1.save() time=1007ms
     * [dc4b7a40] |<--interface hello.proxy.app.v1.OrderServiceV1.orderItem() time=1016ms
     * [dc4b7a40] interface hello.proxy.app.v1.OrderControllerV1.request() time=1018ms
     */
    private static final String[] PATTERNS = {"request", "order*", "save*"};

    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = orderRepositoryV1(logTrace);

        return (OrderControllerV1) Proxy.newProxyInstance(OrderControllerV1.class.getClassLoader(),
                new Class[]{OrderControllerV1.class},
                new LogTraceFilterHandler(new OrderControllerV1Impl(orderServiceV1(logTrace)), logTrace, PATTERNS));

    }

    @Bean
    OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));

        OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(OrderServiceV1.class.getClassLoader(),
                new Class[]{OrderServiceV1.class},
                new LogTraceFilterHandler(orderService, logTrace, PATTERNS));

        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();

        OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(OrderRepositoryV1.class.getClassLoader(),
                new Class[]{OrderRepositoryV1.class},
                new LogTraceFilterHandler(orderRepository, logTrace, PATTERNS));

        return proxy;
    }
}
