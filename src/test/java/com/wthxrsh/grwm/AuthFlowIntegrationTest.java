package com.wthxrsh.grwm;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.wthxrsh.grwm.service.WeatherService;
import com.wthxrsh.grwm.service.WeatherService.Location;
import com.wthxrsh.grwm.service.WeatherService.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WeatherService weatherService;

    private String accessToken;

    @BeforeEach
    void setUp() {
        when(weatherService.geocode(any(String.class)))
                .thenReturn(new Location("Testville", 19.076, 72.8777));
        when(weatherService.reverseGeocode(anyDouble(), anyDouble()))
                .thenReturn("Testville");
        when(weatherService.fetch(any(Location.class)))
                .thenReturn(new WeatherData(26.0, 27.0, 65.0, 8.0, 1, "Mainly clear"));
    }

    @Test
    void registerLoginProfileAndRecommendations() throws Exception {
        String userJson = """
                {
                  "username": "styleuser",
                  "email": "styleuser@example.com",
                  "password": "password123",
                  "firstName": "Style",
                  "lastName": "User",
                  "city": "Testville",
                  "stylePreference": "casual"
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.user.username").value("styleuser"))
                .andReturn();

        accessToken = extractToken(registerResult, "accessToken");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "styleuser", "password": "password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("styleuser@example.com"));

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Testville"));

        mockMvc.perform(post("/api/recommendations/suggest")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"city\": \"Testville\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weatherCondition").value("Mainly clear"))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations[0]").exists())
                .andExpect(jsonPath("$.summary").isNotEmpty());

        mockMvc.perform(get("/api/recommendations/history")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].city").value("Testville"));
    }

    @Test
    void registerWithoutCityAndStyleSucceeds() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "nocity", "email": "nocity@example.com", "password": "password123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        String token = extractToken(registerResult, "accessToken");
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nocity"));
    }

    @Test
    void reverseGeocodeEndpointAndCoordinateBasedSuggest() throws Exception {
        mockMvc.perform(get("/api/locations/reverse")
                        .param("latitude", "40.7128")
                        .param("longitude", "-74.0060"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Testville"));

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "coorduser", "email": "coorduser@example.com", "password": "password123"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String token = extractToken(registerResult, "accessToken");

        mockMvc.perform(post("/api/recommendations/suggest")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latitude\": 40.7128, \"longitude\": -74.0060}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Testville"));
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "loki", "email": "loki@example.com", "password": "password123"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "loki", "password": "wrong-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateUsernameIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "dupuser", "email": "dupuser@example.com", "password": "password123"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "dupuser", "email": "other@example.com", "password": "password123"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "x", "email": "not-an-email", "password": "short"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.username").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    private String extractToken(MvcResult result, String field) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get(field).asText();
    }
}