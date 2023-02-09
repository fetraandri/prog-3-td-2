package integration;

import app.foot.FootApi;
import app.foot.controller.rest.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
class PlayerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Player player1() {
        return Player.builder()
                .id(1)
                .name("J1")
                .isGuardian(false)
                .build();
    }

    Player player2() {
        return Player.builder()
                .id(2)
                .name("J2")
                .isGuardian(false)
                .build();
    }

    Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .isGuardian(false)
                .build();
    }

    @Test
    void read_players_ok() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/players"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(9, actual.size());
        assertTrue(actual.containsAll(List.of(
                player1(),
                player2(),
                player3())));
    }
        @Test
    void create_players_ok() throws Exception {
        Player toCreate = Player.builder()
                .name("Joe Doe")
                .isGuardian(false)
                .teamName("E1")
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(post("/players")
                        .content(objectMapper.writeValueAsString(List.of(toCreate)))
                        .contentType("application/json")
                        .accept("application/json"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(1, actual.size());
        assertEquals(toCreate, actual.get(0).toBuilder().id(null).build());
    }

    @Test
    void modify_players_id_not_found_ko() throws Exception {
        int id = 1000;
        ModifyPlayer toModify = ModifyPlayer.builder()
                .id(id)
                .name("J5modified")
                .isGuardian(false)
                .build();

        String errorMessage = "404 NOT_FOUND : Player#"+id+" not found";

        assertThrowsApiException(errorMessage,
                mockMvc.perform(put("/players")
                                .content(objectMapper.writeValueAsString(List.of(toModify)))
                                .contentType("application/json")
                                .accept("application/json"))
                        .andExpect(status().isNotFound()).andReturn().getResponse());
    }

                .id(5)
                .name("J5modified")
                .isGuardian(false)
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(put("/players")
                        .content(objectMapper.writeValueAsString(List.of(toModify)))
                        .contentType("application/json")
                        .accept("application/json"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);
        Player expected = Player.builder()
                .id(toModify.getId())
                .name(toModify.getName())
                .isGuardian(toModify.getIsGuardian())
                .teamName(actual.get(0).getTeamName()).build();

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }
    @Test
    void modify_players_id_null_ko() throws Exception {
        ModifyPlayer toModify = ModifyPlayer.builder()
                .id(null)
                .name("J5modified")
                .isGuardian(false)
                .build();

        String errorMessage = "400 BAD_REQUEST : Valid Player Id must be specified";

        assertThrowsApiException(errorMessage,
                mockMvc.perform(put("/players")
                                .content(objectMapper.writeValueAsString(List.of(toModify)))
                                .contentType("application/json")
                                .accept("application/json"))
                        .andExpect(status().isBadRequest()).andReturn().getResponse());
    }



    private List<Player> convertFromHttpResponse(MockHttpServletResponse response)
            throws JsonProcessingException, UnsupportedEncodingException {
        CollectionType playerListType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Player.class);
        return objectMapper.readValue(
                response.getContentAsString(),
                playerListType);
    }
}
