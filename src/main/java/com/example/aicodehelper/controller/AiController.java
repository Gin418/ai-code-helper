package com.example.aicodehelper.controller;

import com.example.aicodehelper.ai.AiCodeHelperService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ai")
public class AiController {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @GetMapping("chat")
    public String chat() {
        return aiCodeHelperService.chat("hello");
    }
}
