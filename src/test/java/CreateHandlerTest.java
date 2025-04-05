import org.geronimo.command.context.VoteCreationContext;
import org.geronimo.command.handler.client.CreateHandler;
import org.geronimo.model.Topic;
import org.geronimo.util.ResponseBuilder;
import io.netty.channel.Channel;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CreateHandlerTest {
    private final Map<String, Topic> topics = new HashMap<>();
    private final Map<Channel, VoteCreationContext> contexts = new HashMap<>();
    private final CreateHandler handler = new CreateHandler(topics, contexts);
    private final Channel channel = mock(Channel.class);
    private final String username = "testUser";

    @Test
    void shouldCreateTopicSuccessfully() {
        String response = handler.handle(channel, "topic -n=newTopic", username);
        assertEquals(ResponseBuilder.success("Раздел создан"), response);
        assertTrue(topics.containsKey("newTopic"));
    }

    @Test
    void shouldRejectDuplicateTopic() {
        topics.put("existing", new Topic("existing", username, new HashMap<>()));
        String response = handler.handle(channel, "topic -n=existing", username);
        assertEquals(ResponseBuilder.error("Раздел уже существует"), response);
    }

    @Test
    void shouldStartVoteCreationProcess() {
        topics.put("testTopic", new Topic("testTopic", username, new HashMap<>()));
        String response = handler.handle(channel, "vote -t=testTopic", username);
        assertEquals(ResponseBuilder.expect("Введите название голосования:"), response);
        assertNotNull(contexts.get(channel));
    }
}
