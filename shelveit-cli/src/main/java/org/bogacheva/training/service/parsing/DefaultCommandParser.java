package org.bogacheva.training.service.parsing;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class DefaultCommandParser implements CommandParser {
    
    private static final Pattern WHOLE_COMMAND_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
    
    @Override
    public ParsedCommand parse(String input) {
        String[] parts = splitInParts(input);
        String commandType = parseCommandType(parts);
        
        // Special handling for help and exit commands 
        if ("help".equals(commandType) || "exit".equals(commandType)) {
            return new ParsedCommand(commandType, parts, Collections.emptyMap());
        }
        
        int start = getArgsStartIndex(commandType);
        Map<String, String> args = parseArguments(Arrays.copyOfRange(parts, start, parts.length));
        
        return new ParsedCommand(commandType, parts, args);
    }
    
    private int getArgsStartIndex(String commandType) {
        return switch (commandType) {
            case "get items by storage" -> 4;
            case "get items near" -> 3;
            default -> 2;
        };
    }
    
    private String[] splitInParts(String input) {
        List<String> parts = new ArrayList<>();
        Matcher m = WHOLE_COMMAND_PATTERN.matcher(input);
        while (m.find()) {
            // For quoted strings, use group 1; otherwise, use group 2
            parts.add(m.group(1) != null ? m.group(1) : m.group(2));
        }
        return parts.toArray(new String[0]);
    }
    
    private String parseCommandType(String[] parts) {
        if (parts.length == 0) {
            return "broken";
        }
        if (parts.length == 1) {
            String command = parts[0].toLowerCase();
            // Special handling for single-word commands
            if ("help".equals(command) || "exit".equals(command)) {
                return command;
            }
            return command;
        }
        String cmdCandidate = (parts[0] + " " + parts[1]).toLowerCase();
        return getCommandType(parts, cmdCandidate);
    }
    
    private String getCommandType(String[] parts, String cmdCandidate) {
        if ("get items".equals(cmdCandidate)) {
            if (parts.length > 3 && parts[2].equalsIgnoreCase("by") && parts[3].equalsIgnoreCase("storage")) {
                return "get items by storage";
            }
            if (parts.length > 2 && parts[2].equalsIgnoreCase("near")) {
                return "get items near";
            }
        }
        return cmdCandidate;
    }
    
    private Map<String, String> parseArguments(String[] parts) {
        Map<String, String> args = new HashMap<>();
        String currentKey = null;

        for (String part : parts) {
            if (part.startsWith("--")) {
                currentKey = part.substring(2).toLowerCase(); // Remove "--" prefix
                args.put(currentKey, ""); // Initialize with empty value
            } else if (currentKey != null) {
                args.put(currentKey, args.get(currentKey).isEmpty()
                        ? part.trim()
                        : args.get(currentKey) + " " + part.trim());
            } else {
                throw new IllegalArgumentException("Unexpected argument: " + part);
            }
        }
        return args;
    }
}
