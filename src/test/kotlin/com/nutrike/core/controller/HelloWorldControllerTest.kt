import com.nutrike.core.controller.HelloWorldController
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class HelloWorldControllerTest {
    private var mockMvc: MockMvc
    private var helloWorldController: HelloWorldController = HelloWorldController()

    init {
        mockMvc = MockMvcBuilders.standaloneSetup(helloWorldController).build()
    }

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
