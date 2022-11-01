package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping//@Controller or @RequestMapping이 존재시 스프링 Controller로 인식 가능
@ResponseBody
public interface OrderControllerV1 {
    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);
    //Interface에서는 @ReqeustParam을 넣어줘야 인식이 잘 된다. (버전 등 설정문제)

    @GetMapping("/v1/no-log")
    String noLog();
}

