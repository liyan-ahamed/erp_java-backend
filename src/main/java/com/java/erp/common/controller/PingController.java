package com.java.erp.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * A simple controller to verify the backend is running.
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    public Map<String, String> ping() {
        return Map.of("status", "UP", "message", "pong!");
    }
}
