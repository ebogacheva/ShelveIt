package org.bogacheva.training.view;

import org.springframework.stereotype.Component;

@Component
public class ConsolePrinter implements Printer<String> {
    @Override
    public void print(String text) {
        System.out.print(text);
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }
}
