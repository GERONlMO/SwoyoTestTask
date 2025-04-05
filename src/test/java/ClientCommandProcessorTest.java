import io.netty.channel.Channel;
import org.geronimo.command.ClientCommandProcessor;
import org.geronimo.model.Topic;
import org.geronimo.util.ResponseBuilder;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ClientCommandProcessorTest {
    private final Map<String, Topic> topics = new HashMap<>();
    private final ClientCommandProcessor processor = new ClientCommandProcessor(topics);
    private final Channel channel = mock(Channel.class);

    @Test
    void shouldHandleFullFlow() {
        String loginResponse = processor.processCommand(channel, "login -u=test");
        assertEquals(ResponseBuilder.success("Авторизация прошла успешно"), loginResponse);

        String createResponse = processor.processCommand(channel, "create topic -n=testTopic");
        assertEquals(ResponseBuilder.success("Раздел создан"), createResponse);

        String viewResponse = processor.processCommand(channel, "view");
        assertTrue(viewResponse.contains("testTopic"));
    }
}