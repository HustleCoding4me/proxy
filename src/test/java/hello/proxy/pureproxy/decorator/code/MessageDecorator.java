package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecorator extends Decorator {

    public MessageDecorator(Component component) {
        super(component);
    }

    @Override
    public String operation() {
        log.info("MessageDecorator 실행");

        //data -> ****** data ******로 추가
        String resultData = super.operation();
        String decoResult = "****** " + resultData + " ******";
        log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={})",resultData, decoResult);

        return  decoResult;
    }

}
