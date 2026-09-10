package pathfinder.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import pathfinder.exception.PathfinderException;
import pathfinder.task.DeadlineTask;
import pathfinder.task.EventTask;
import pathfinder.task.TodoTask;

/** Tests Pathfinder's command interpretation and validation rules. */
class ParserTest {
    @Test
    void parseCommandWord_uppercaseCommand_returnsLowercaseWord() throws PathfinderException {
        assertEquals("list", Parser.parseCommandWord("LIST"));
    }

    @Test
    void parseCommandWord_commandWithArguments_returnsFirstWord() throws PathfinderException {
        assertEquals("deadline", Parser.parseCommandWord("deadline return book /by 2019-12-02"));
    }

    @Test
    void parseCommandWord_emptyInput_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () -> Parser.parseCommandWord(""));
        assertEquals("Oh no friend! You didn't enter anything!", exception.getMessage());
    }

    @Test
    void requireNoArguments_commandOnly_acceptsDifferentCase() {
        assertDoesNotThrow(() -> Parser.requireNoArguments("LiSt", "list"));
    }

    @Test
    void requireNoArguments_extraWords_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () -> Parser.requireNoArguments("list extra", "list"));
        assertEquals("Oopsies! The list command does not take extra words.",
                exception.getMessage());
    }

    @Test
    void parseTodo_validDescription_returnsTodo() throws PathfinderException {
        TodoTask task = Parser.parseTodo("todo read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    void parseTodo_tabSeparator_returnsTodo() throws PathfinderException {
        TodoTask task = Parser.parseTodo("todo\tread book");

        assertEquals("read book", task.getDescription());
    }

    @Test
    void parseTodo_emptyDescription_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () -> Parser.parseTodo("todo"));
        assertEquals("Oopsies! A todo needs a description, friend!", exception.getMessage());
    }

    @Test
    void parseTodo_mismatchedCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Parser.parseTodo("list read book"));
    }

    @Test
    void parseTaskNumber_positiveWholeNumber_returnsNumber() throws PathfinderException {
        assertEquals(12, Parser.parseTaskNumber("mark 12", "mark"));
    }

    @Test
    void parseTaskNumber_nonNumericValue_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () -> Parser.parseTaskNumber("mark twelve", "mark"));
        assertEquals("Oopsies! Please provide one positive whole task number.",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_tooLargeValue_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () ->
                        Parser.parseTaskNumber("mark 99999999999999999999", "mark"));
        assertEquals("Oopsies! That task number is too large.", exception.getMessage());
    }

    @Test
    void parseFindKeyword_singleWord_returnsKeyword() throws PathfinderException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    void parseFindKeyword_multipleWords_returnsTrimmedKeyword() throws PathfinderException {
        assertEquals("project meeting", Parser.parseFindKeyword("find   project meeting   "));
    }

    @Test
    void parseFindKeyword_emptyKeyword_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () -> Parser.parseFindKeyword("find    "));
        assertEquals("Oopsies! The find command needs a keyword, friend!",
                exception.getMessage());
    }

    @Test
    void parseDeadline_validCommand_returnsDeadline() throws PathfinderException {
        DeadlineTask task = Parser.parseDeadline("deadline return book /by 2/12/2019 1800");

        assertEquals("return book", task.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), task.getBy());
    }

    @Test
    void parseDeadline_missingByMarker_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () -> Parser.parseDeadline("deadline return book"));
        assertEquals("Oopsies! A deadline needs '/by' followed by a date or time.",
                exception.getMessage());
    }

    @Test
    void parseEvent_validCommand_returnsEvent() throws PathfinderException {
        EventTask task = Parser.parseEvent(
                "event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600");

        assertEquals("project meeting", task.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 3, 14, 0), task.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), task.getTo());
    }

    @Test
    void parseEvent_markersInWrongOrder_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () -> Parser.parseEvent(
                        "event meeting /to 2019-12-03 1600 /from 2019-12-03 1400"));
        assertEquals("Oopsies! An event needs '/from' before '/to'.", exception.getMessage());
    }

    @Test
    void parseEvent_endNotAfterStart_throwsPathfinderException() {
        PathfinderException exception = assertThrows(
                PathfinderException.class, () -> Parser.parseEvent(
                        "event meeting /from 2019-12-03 1600 /to 2019-12-03 1600"));
        assertEquals("Oopsies! An event's '/to' time must be after its '/from' time.",
                exception.getMessage());
    }
}
