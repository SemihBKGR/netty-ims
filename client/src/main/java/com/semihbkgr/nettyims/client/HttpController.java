package com.semihbkgr.nettyims.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HttpController {

    private final ServerStatusService serverStatusService;

    @GetMapping("/chat")
    public Mono<String> chat(Model model) {
        return serverStatusService.getStatus()
                .map(serverStatusResponse -> {
                    log.info("ServerStatus: {}", serverStatusResponse);
                    model.addAttribute("serverStatus", serverStatusResponse);
                    return "chat";
                });
    }

    @GetMapping("/**")
    public Mono<String> redirect(){
        return Mono.just("redirect:/chat");
    }

}
