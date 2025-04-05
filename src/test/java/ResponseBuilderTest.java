import org.geronimo.util.ResponseBuilder;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ResponseBuilderTest {

    @Test
    void shouldFormatErrorResponse() {
        String result = ResponseBuilder.error("Недостаточно прав");
        assertEquals("ОШИБКА: Недостаточно прав", result);
    }

    @Test
    void shouldFormatSuccessResponse() {
        String result = ResponseBuilder.success("Данные сохранены");
        assertEquals("УСПЕХ: Данные сохранены", result);
    }

    @Test
    void shouldFormatVoteResults() {
        String output = ResponseBuilder.formatVoteResults(
                "Фильмы",
                "Лучшие фильмы 2024",
                Map.of(0, 5, 1, 3),
                List.of("Анора", "Субстанция")
        );

        assertTrue(output.contains("Анора: 5"));
        assertTrue(output.contains("Субстанция: 3"));
        assertTrue(output.startsWith("Тема: Фильмы"));
    }

    @Test
    void shouldHandleEmptyResults() {
        String output = ResponseBuilder.formatVoteResults(
                "Тест",
                "Пустое голосование",
                Map.of(),
                List.of()
        );

        assertTrue(output.contains("Результаты:"));
    }
}