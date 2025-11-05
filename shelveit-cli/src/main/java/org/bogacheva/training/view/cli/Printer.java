package org.bogacheva.training.view.cli;

public interface Printer<T> {
    void print(T value);
    void println(T value);
}
