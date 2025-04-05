import org.geronimo.util.CommandParser;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    @Test
    void shouldExtractArgumentValue() {
        String input = "create -t=mytopic -n=vote1";
        Optional<String> result = CommandParser.getArgumentValue(input, "-t");
        assertEquals("mytopic", result.get());
    }

    @Test
    void shouldReturnEmptyForMissingArgument() {
        String input = "view -v=vote1";
        Optional<String> result = CommandParser.getArgumentValue(input, "-t");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldExtractValueAfterFlag() {
        String input = "delete -t mytopic -v vote1";
        Optional<String> result = CommandParser.getValueAfterFlag(input, "-t");
        assertEquals("mytopic", result.get());
    }

    @Test
    void shouldHandleSpecialCharacters() {
        String input = "login -u=user@domain.com";
        Optional<String> result = CommandParser.getArgumentValue(input, "-u");
        assertEquals("user@domain.com", result.get());
    }
}