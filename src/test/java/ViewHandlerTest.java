import org.geronimo.command.handler.client.ViewHandler;
import org.geronimo.model.Topic;
import org.geronimo.model.Vote;
import org.geronimo.util.ResponseBuilder;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ViewHandlerTest {
    private final Map<String, Topic> topics = new HashMap<>();
    private final ViewHandler handler = new ViewHandler(topics);

    @Test
    void shouldListAllTopics() {
        topics.put("topic1", new Topic("topic1", "user", new HashMap<>()));
        String response = handler.handle("");
        assertTrue(response.contains("topic1"));
    }

    @Test
    void shouldShowVoteResults() {
        Topic topic = new Topic("test", "user", new HashMap<>());
        topic.addVote(new Vote("vote1", "desc", List.of("a", "b"), "user", Map.of(0, 5)));
        topics.put("test", topic);

        String response = handler.handle("view -t=test -v=vote1");
        assertTrue(response.contains("a: 5 голосов"));
    }
}
