package org.bogacheva.training.view.cli.help;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of HelpTextProvider that loads help text from individual resource files.
 */
@Component
public class DefaultHelpTextProvider implements HelpTextProvider {
    
    private final ResourceLoader resourceLoader;
    private final Map<String, String> helpTextCache = new ConcurrentHashMap<>();
    
    public DefaultHelpTextProvider(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public String getHelpText() {
        return loadHelpText("general-help.txt");
    }
    
    @Override
    public String getCommandHelp(String command) {
        String fileName = command.toLowerCase().replace(" ", "-") + ".txt";
        return loadHelpText(fileName);
    }
    

    private String loadHelpText(String fileName) {
        return helpTextCache.computeIfAbsent(fileName, this::loadHelpTextFromResource);
    }
    
    private String loadHelpTextFromResource(String fileName) {
        try {
            Resource resource = resourceLoader.getResource("classpath:help/" + fileName);
            if (!resource.exists()) {
                throw new IllegalStateException("Help text file not found: " + fileName);
            }
            
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load help text from file: " + fileName, e);
        }
    }
}