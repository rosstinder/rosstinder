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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class FavoriteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void makeLikeOrDislikeSuccessTest() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/favorites/111")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Вы любимы")));
    }

    @Test
    void findNextFavoriteWhenNoFavoritesPresentTest() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/favorites/115/next")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Пользователь chatId=115 не проявлял ни к кому любви.")));
    }
}
