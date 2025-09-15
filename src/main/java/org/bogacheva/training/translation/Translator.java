package org.bogacheva.training.translation;

public interface Translator<IN, OUT> {
    OUT translate (IN value);
}
