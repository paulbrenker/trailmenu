import com.nutrike.core.controller.HelloWorldController
import com.nutrike.core.util.BaseControllerTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.get

@SpringBootTest(classes = [HelloWorldController::class])
class HelloWorldControllerTest : BaseControllerTest() {
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
