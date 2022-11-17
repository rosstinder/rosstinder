package org.rosstinder.prerevolutionarytinderserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getStatusOfNonExistedUser() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/users/10/status")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status", is("406 NOT_ACCEPTABLE")))
                .andExpect(jsonPath("$.message", is("Пользователь chatId=10 не был найден.")));
    }

    @Test
    void updateProfileWrongValueTest() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/users/111")
                .queryParam("key", "preference")
                .queryParam("value", "Никого")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status", is("406 NOT_ACCEPTABLE")))
                .andExpect(jsonPath("$.message", is("Неправильный формат предпочтения. Допустимые значения: Сударъ, Сударыня, Всех.")));
    }
}
