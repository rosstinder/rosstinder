package org.rosstinder.prerevolutionarytinderserver;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rosstinder.prerevolutionarytinderserver.controller.FavoriteController;
import org.rosstinder.prerevolutionarytinderserver.controller.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PrerevolutionaryTinderServerApplicationTests {

    @Autowired
    private UserController userController;

    @Autowired
    private FavoriteController favoriteController;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testUserControllerIsNotNull() {
        Assertions.assertThat(userController).isNotNull();
    }

    @Test
    void testFavoriteControllerIsNotNull() {
        Assertions.assertThat(favoriteController).isNotNull();
    }

}
