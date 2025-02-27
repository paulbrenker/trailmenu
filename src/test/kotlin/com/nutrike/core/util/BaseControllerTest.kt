package com.nutrike.core.util

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

/**
 * Starts an Integration Test without securing Application Endpoints with JwtToken Authentication.
 *
 * Should not be used if you specifically test Authentication.
 */
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc(addFilters = false)
abstract class BaseControllerTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc
}
