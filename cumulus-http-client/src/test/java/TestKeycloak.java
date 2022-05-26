import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;

class TestKeycloak {

    @Test
    void testGetToken() {
        Keycloak instance = Keycloak.getInstance("https://auth.corps.cloud/auth", "water", "", "","cumulus");
        TokenManager tokenmanager = instance.tokenManager();
        String accessToken = tokenmanager.getAccessTokenString();
        assertNotNull(accessToken);
    }
}
