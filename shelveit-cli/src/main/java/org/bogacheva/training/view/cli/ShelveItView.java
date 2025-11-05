package org.bogacheva.training.view.cli;

import java.util.List;

/**
 * View interface for console input/output.
 * Defines methods to print messages, errors, lists, and read user input.
 */
public interface ShelveItView {

    void printHeader();
    void printExit();
    void printPrompt();
    <T> void  print(List<T> items);
    String readCommand();
    void printError(String message);
}
