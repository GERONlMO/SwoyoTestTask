import org.geronimo.command.handler.client.LoginHandler;
import org.geronimo.model.User;
import org.geronimo.util.ResponseBuilder;
import io.netty.channel.Channel;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LoginHandlerTest {
    private final Map<Channel, User> sessions = new HashMap<>();
    private final LoginHandler handler = new LoginHandler(sessions);
    private final Channel channel = mock(Channel.class);

    @Test
    void shouldLoginSuccessfully() {
        String response = handler.handle(channel, "login -u=testUser");
        assertEquals(ResponseBuilder.success("Авторизация прошла успешно"), response);
        assertEquals("testUser", sessions.get(channel).getUsername());
    }

    @Test
    void shouldHandleInvalidFormat() {
        String response = handler.handle(channel, "login wrong_format");
        assertTrue(response.startsWith("ОШИБКА"));
        assertNull(sessions.get(channel));
    }
}
