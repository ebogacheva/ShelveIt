package org.bogacheva.training.service.translation;

public interface Translator<IN, OUT> {
    OUT translate (IN value);
}
