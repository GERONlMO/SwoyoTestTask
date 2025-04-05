import org.geronimo.command.ServerCommandProcessor;
import org.geronimo.model.Topic;
import org.geronimo.util.ResponseBuilder;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ServerCommandProcessorTest {
    private final Map<String, Topic> topics = new HashMap<>();
    private final ServerCommandProcessor processor = new ServerCommandProcessor(topics);

    @Test
    void shouldHandleSaveCommand() {
        topics.put("test", new Topic("test", "admin", new HashMap<>()));
        String response = processor.processCommand("save data.dat");
        assertTrue(response.startsWith("УСПЕХ"));
    }

    @Test
    void shouldHandleLoadCommand() {
        String response = processor.processCommand("load data.dat");
        assertTrue(response.startsWith("УСПЕХ") || response.startsWith("ОШИБКА"));
    }
}