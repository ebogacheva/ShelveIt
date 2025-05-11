package org.bogacheva.training.service.translators;

public interface Translator<IN, OUT> {
    OUT translate (IN value);
}
