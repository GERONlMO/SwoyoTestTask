import org.geronimo.command.handler.client.DeleteHandler;
import org.geronimo.model.Topic;
import org.geronimo.model.User;
import org.geronimo.model.Vote;
import org.geronimo.util.ResponseBuilder;
import io.netty.channel.Channel;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DeleteHandlerTest {
    private final Map<String, Topic> topics = new HashMap<>();
    private final Map<Channel, User> sessions = new HashMap<>();
    private final DeleteHandler handler = new DeleteHandler(topics, sessions);
    private final Channel channel = mock(Channel.class);

    @Test
    void shouldDeleteVoteSuccessfully() {
        Topic topic = new Topic("test", "owner", new HashMap<>());
        topic.addVote(new Vote("vote1", "desc", List.of("a", "b"), "owner", new HashMap<>()));
        topics.put("test", topic);
        sessions.put(channel, new User("owner"));

        String response = handler.handle(channel, "delete -t=test -v=vote1");
        assertEquals(ResponseBuilder.success("Голосование удалено"), response);
        assertNull(topic.getVote("vote1"));
    }
}