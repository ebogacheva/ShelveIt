package org.bogacheva.training.view.cli.help;

/**
 * Interface for providing help text content.
 */
public interface HelpTextProvider {
    
    String getHelpText();
    
    String getCommandHelp(String command);
}
