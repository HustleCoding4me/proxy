package hello.proxy.config.v1_proxy.concreteproxy;

import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class OrderRepositoryConcrreteProxy extends OrderRepositoryV2 {

        private final OrderRepositoryV2 target;
    private final LogTrace logTrace;


        @Override
        public void save(String itemId) {
            TraceStatus status = logTrace.begin("OrderRepository.save()");
            //target 호출
            target.save(itemId);
            logTrace.end(status);
        }
}
