package org.bogacheva.training.view.cli.parsing;

import java.util.Map;

public record ParsedCommand(String commandType, String[] commandParts, Map<String, String> arguments) {}
