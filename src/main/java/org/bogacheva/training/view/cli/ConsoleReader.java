package org.bogacheva.training.view.cli;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleReader implements Reader<String> {
    private static final Scanner SCANNER = new Scanner(System.in);
    @Override
    public String read() {
        return SCANNER.nextLine();
    }
}
