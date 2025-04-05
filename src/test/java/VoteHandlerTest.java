import org.geronimo.command.context.VoteContext;
import org.geronimo.command.handler.client.VoteHandler;
import org.geronimo.model.Topic;
import org.geronimo.model.Vote;
import org.geronimo.util.ResponseBuilder;
import io.netty.channel.Channel;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class VoteHandlerTest {
    private final Map<Channel, VoteContext> contexts = new HashMap<>();
    private final Map<String, Topic> topics = new HashMap<>();
    private final VoteHandler handler = new VoteHandler(contexts, topics);
    private final Channel channel = mock(Channel.class);

    @Test
    void shouldProcessVoteCorrectly() {
        Topic topic = new Topic("test", "user", new HashMap<>());
        Vote vote = new Vote("vote1", "desc", List.of("a", "b"), "user", new HashMap<>());
        topic.addVote(vote);
        topics.put("test", topic);

        handler.handleStartVote(channel, "vote -t=test -v=vote1");
        String response = handler.processVote(channel, "1");

        assertEquals(ResponseBuilder.success("Голос учтен"), response);
        assertEquals(1, vote.getResults().get(0));
    }
}