import com.nutrike.core.controller.HelloWorldController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(classes = [HelloWorldController::class])
@AutoConfigureMockMvc
class HelloWorldControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) {
        @Test
        fun `should return Hello World on hello-world endpoint`() {
            mockMvc
                .get("/hello-world")
                .andExpect { status { isOk() } }
                .andExpect { content { string("Hello World!") } }
        }

        @Test
        fun `should return not Found on non defined endpoint`() {
            mockMvc
                .get("/not-found")
                .andExpect { status { isNotFound() } }
        }
    }
