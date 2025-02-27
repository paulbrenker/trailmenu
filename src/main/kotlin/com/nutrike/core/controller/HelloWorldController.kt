package com.nutrike.core.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Example Endpoint", description = "Just one Example")
class HelloWorldController {
    @GetMapping("/hello-world")
    fun helloWorld() = "Hello World!"
}
