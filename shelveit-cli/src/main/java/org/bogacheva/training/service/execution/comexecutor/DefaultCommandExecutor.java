package org.bogacheva.training.service.execution.comexecutor;

import lombok.RequiredArgsConstructor;
import org.bogacheva.training.service.command.*;
import org.bogacheva.training.service.execution.rest.ServiceCaller;
import org.bogacheva.training.view.cli.help.HelpTextProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DefaultCommandExecutor implements CommandExecutor {
    
    private final ServiceCaller serviceCaller;
    private final HelpTextProvider helpTextProvider;
    
    @Override
    public CommandExecutionResult execute(BaseCommand command) {
        if (command instanceof BrokenCommand brokenCommand) {
            return new CommandExecutionResult(false, "Broken command: " + brokenCommand.getErrorMessage());
        }
        
        return switch (command) {
            case CreateItemCommand cmd -> createResult(
                List.of(serviceCaller.createItem(cmd.getCreateItemDTO())), 
                "Created item:"
            );
            
            case CreateStorageCommand cmd -> createResult(
                List.of(serviceCaller.createStorage(cmd.getStorageCreateDTO())), 
                "Created storage:"
            );
            
            case ListItemsCommand ignored -> new CommandExecutionResult(serviceCaller.getAllItems(), false);
            case ListStoragesCommand ignored -> new CommandExecutionResult(serviceCaller.getAllStorages(), false);
            
            case RemoveItemCommand cmd -> {
                serviceCaller.deleteItem(cmd.getId());
                yield new CommandExecutionResult(false, "Item deleted successfully");
            }
            
            case RemoveStorageCommand cmd -> {
                serviceCaller.deleteStorage(cmd.getId());
                yield new CommandExecutionResult(false, "Storage deleted successfully");
            }
            
            case GetItemsByStorageCommand cmd -> new CommandExecutionResult(
                serviceCaller.getItemsByStorageId(cmd.getStorageId()), false
            );
            
            case ListSubStoragesCommand cmd -> new CommandExecutionResult(
                serviceCaller.getSubStorages(cmd.getStorageId()), false
            );
            
            case GetItemByIdCommand cmd -> new CommandExecutionResult(
                List.of(serviceCaller.getItemById(cmd.getId())), false
            );
            
            case GetStorageByIdCommand cmd -> new CommandExecutionResult(
                List.of(serviceCaller.getStorageById(cmd.getId())), false
            );
            
            case SearchItemCommand cmd -> new CommandExecutionResult(
                serviceCaller.searchItems(cmd.getName(), cmd.getKeywords()), false
            );
            
            case SearchStorageCommand cmd -> new CommandExecutionResult(
                serviceCaller.searchStorages(cmd.getName(), cmd.getType()), false
            );
            
            case GetItemsNearCommand cmd -> new CommandExecutionResult(
                serviceCaller.getItemsNear(cmd.getItemId()), false
            );
            
            case TrackStoragesHierarchyCommand cmd -> new CommandExecutionResult(
                serviceCaller.getStorageHierarchyIds(cmd.getItemId()), false
            );
            
            case HelpCommand cmd -> new CommandExecutionResult(false, 
                cmd.isGeneralHelp() 
                    ? helpTextProvider.getHelpText()
                    : helpTextProvider.getCommandHelp(cmd.getCommand())
            );
            
            case ExitCommand ignored -> new CommandExecutionResult(true);
            default -> new CommandExecutionResult(false, "Unknown command. Please try again.");
        };
    }
    
    private CommandExecutionResult createResult(List<?> data, String message) {
        return new CommandExecutionResult(data, false, message);
    }
}
