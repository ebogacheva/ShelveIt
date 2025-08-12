package org.bogacheva.training.view;

import java.util.List;

public interface ShelveItView {

    void printHeader();
    void printExit();
    void printPrompt();
    <T> void  print(List<T> items);
    String readCommand();
    void printError(String message);
}
