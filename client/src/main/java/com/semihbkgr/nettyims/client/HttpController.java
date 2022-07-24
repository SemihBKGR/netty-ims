package com.semihbkgr.nettyims.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class HttpController {

    @Value("${netty-ims.proxy.address}")
    private String nettyIMSProxyAddress;

    @GetMapping("/chat")
    public Mono<String> chat(Model model) {
        model.addAttribute("netty_ims_proxy_address", nettyIMSProxyAddress);
        return Mono.just("chat");
    }

}
