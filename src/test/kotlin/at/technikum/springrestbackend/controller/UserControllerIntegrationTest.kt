package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

//    @Test
//    fun `should return user by ID`() {
//        val user = User(id = 1L, name = "John Doe", email = "john.doe@example.com")
//        Mockito.`when`(userService.getUserById(1L)).thenReturn(user)
//
//        mockMvc.perform(get("/users/1").accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.name").value("John Doe"))
//            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
//    }
}
