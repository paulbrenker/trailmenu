package com.nutrike.core.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should allow access to a public endpoint`() {
        mockMvc
            .get("/actuator")
            .andExpect { status { isOk() } }
    }

    /* TODO: Fix flaky test
    @Test
    fun `should return 401 for unauthenticated access to protected endpoint`() {
        mockMvc
            .get("/secured")
            .andExpect { status { isUnauthorized() } }
    }
     */

    @Test
    @WithMockUser(username = "testuser", roles = ["USER"])
    fun `should be ok for authenticated users`() {
        mockMvc
            .get("/hello-world")
            .andExpect { status { isOk() } }
    }
}
