package com.nutrike.core.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {
    @RequestMapping("/hello-world")
    fun helloWorld() = "Hello World!"
}