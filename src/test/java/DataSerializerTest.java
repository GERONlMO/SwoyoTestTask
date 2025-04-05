import org.geronimo.model.Topic;
import org.geronimo.util.DataSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DataSerializerTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadData() throws IOException {
        Map<String, Topic> original = new HashMap<>();
        original.put("test", new Topic("test", "admin", new HashMap<>()));

        File testFile = tempDir.resolve("test.json").toFile();

        DataSerializer.save(original, testFile.getAbsolutePath());

        Map<String, Topic> loaded = DataSerializer.load(testFile.getAbsolutePath());

        assertEquals(original.size(), loaded.size());
        assertEquals("test", loaded.get("test").getName());
    }

    @Test
    void shouldThrowOnInvalidFile() {
        assertThrows(IOException.class, () ->
                DataSerializer.load("nonexistent.json")
        );
    }
}