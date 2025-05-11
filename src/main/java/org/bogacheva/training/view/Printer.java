package org.bogacheva.training.view;

public interface Printer<T> {
    void print(T value);
    void println(T value);
}
