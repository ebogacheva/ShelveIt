package org.bogacheva.training;

import org.bogacheva.training.ai.AiAssistant;
import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.translation.StringToCommandTranslator;
import org.bogacheva.training.view.cli.ShelveItView;
import org.bogacheva.training.view.cli.commands.BaseCommand;
import org.bogacheva.training.view.cli.execution.CommandExecutionResult;
import org.bogacheva.training.view.cli.execution.CommandExecutor;
import org.bogacheva.training.view.cli.formatting.OutputFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelveItCommandLineRunnerTest {

    @Mock private ShelveItView view;
    @Mock private StringToCommandTranslator translator;
    @Mock private CommandExecutor executor;
    @Mock private OutputFormatter formatter;
    @Mock private AiAssistant aiAssistant;
    @Mock private BaseCommand baseCommand;

    private ShelveItCommandLineRunner runnerWith(AiAssistant ai) {
        ShelveItCommandLineRunner r = new ShelveItCommandLineRunner(
                view, translator, executor, formatter, Optional.ofNullable(ai));
        r.exitAction = () -> {};
        return r;
    }

    private void setupExitOnSecondRead() {
        when(translator.translate(any())).thenReturn(baseCommand);
        when(executor.execute(any())).thenReturn(new CommandExecutionResult(true));
    }

    @Test
    @DisplayName("input starting with ? calls aiAssistant.findItems with trimmed text")
    void run_whenInputStartsWithQuestion_callsAiAssistantWithTrimmedQuery() {
        List<ItemDTO> found = List.of(new ItemDTO());
        when(aiAssistant.findItems("red jacket")).thenReturn(found);
        when(view.readCommand()).thenReturn("? red jacket", "exit");
        setupExitOnSecondRead();

        runnerWith(aiAssistant).run();

        verify(aiAssistant).findItems("red jacket");
        verify(translator, never()).translate("? red jacket");
    }

    @Test
    @DisplayName("? with surrounding spaces trims query before calling findItems")
    void run_whenQueryHasSpacesAfterQuestionMark_trimsBeforeCallingFindItems() {
        when(aiAssistant.findItems("red jacket")).thenReturn(Collections.emptyList());
        when(view.readCommand()).thenReturn("?   red jacket  ", "exit");
        setupExitOnSecondRead();

        runnerWith(aiAssistant).run();

        verify(aiAssistant).findItems("red jacket");
    }

    @Test
    @DisplayName("? query when aiAssistant is not configured prints error and does not call findItems")
    void run_whenAiAssistantNotConfiguredAndQueryStartsWithQuestion_printsError() {
        when(view.readCommand()).thenReturn("? red jacket", "exit");
        setupExitOnSecondRead();

        runnerWith(null).run();

        verify(view).printError("AI assistant is not configured");
        verify(aiAssistant, never()).findItems(any());
    }

    @Test
    @DisplayName("regular command is routed through translator and executor, not through AI")
    void run_whenRegularCommand_routesThroughTranslatorNotAi() {
        when(view.readCommand()).thenReturn("list items");
        when(translator.translate("list items")).thenReturn(baseCommand);
        when(executor.execute(baseCommand)).thenReturn(new CommandExecutionResult(true));

        runnerWith(aiAssistant).run();

        verify(translator).translate("list items");
        verify(aiAssistant, never()).findItems(any());
    }
}
