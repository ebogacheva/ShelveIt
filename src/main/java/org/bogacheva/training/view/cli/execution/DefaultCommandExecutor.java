package org.bogacheva.training.view.cli.execution;

import org.bogacheva.training.view.cli.commands.*;
import org.bogacheva.training.view.cli.help.HelpTextProvider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Default implementation of CommandExecutor.
 * Handles the execution logic for all command types.
 */
@Component
public class DefaultCommandExecutor implements CommandExecutor {
    
    private final ServiceCaller serviceCaller;
    private final HelpTextProvider helpTextProvider;
    
    public DefaultCommandExecutor(ServiceCaller serviceCaller, HelpTextProvider helpTextProvider) {
        this.serviceCaller = serviceCaller;
        this.helpTextProvider = helpTextProvider;
    }
    
    @Override
    public CommandExecutionResult execute(BaseCommand command) {
        if (command instanceof BrokenCommand brokenCommand) {
            return new CommandExecutionResult(false, "Broken command: " + brokenCommand.getErrorMessage());
        }
        
        return switch (command) {
            case CreateItemCommand createItem -> {
                var newItem = serviceCaller.createItem(createItem.getCreateItemDTO());
                yield new CommandExecutionResult(List.of(newItem), false, "Created item:");
            }
            
            case CreateStorageCommand createStorage -> {
                var newStorage = serviceCaller.createStorage(createStorage.getStorageCreateDTO());
                yield new CommandExecutionResult(List.of(newStorage), false, "Created storage:");
            }
            
            case ListItemsCommand ignored -> {
                var items = serviceCaller.getAllItems();
                yield new CommandExecutionResult(items, false);
            }
            
            case ListStoragesCommand ignored -> {
                var storages = serviceCaller.getAllStorages();
                yield new CommandExecutionResult(storages, false);
            }
            
            case RemoveItemCommand removeItem -> {
                serviceCaller.deleteItem(removeItem.getId());
                yield new CommandExecutionResult(false, "Item deleted successfully");
            }
            
            case RemoveStorageCommand removeStorage -> {
                serviceCaller.deleteStorage(removeStorage.getId());
                yield new CommandExecutionResult(false, "Storage deleted successfully");
            }
            
            case GetItemsByStorageCommand getItemsByStorage -> {
                var items = serviceCaller.getItemsByStorageId(getItemsByStorage.getStorageId());
                yield new CommandExecutionResult(items, false);
            }
            
            case ListSubStoragesCommand listSubStorages -> {
                var subStorages = serviceCaller.getSubStorages(listSubStorages.getStorageId());
                yield new CommandExecutionResult(subStorages, false);
            }
            
            case GetItemByIdCommand getItemById -> {
                var item = serviceCaller.getItemById(getItemById.getId());
                yield new CommandExecutionResult(List.of(item), false);
            }
            
            case GetStorageByIdCommand getStorageById -> {
                var storage = serviceCaller.getStorageById(getStorageById.getId());
                yield new CommandExecutionResult(List.of(storage), false);
            }
            
            case SearchItemCommand searchItem -> {
                var results = serviceCaller.searchItems(searchItem.getName(), searchItem.getKeywords());
                yield new CommandExecutionResult(results, false);
            }
            
            case GetItemsNearCommand getItemsNear -> {
                var items = serviceCaller.getItemsNear(getItemsNear.getItemId());
                yield new CommandExecutionResult(items, false);
            }
            
            case TrackStoragesHierarchyCommand trackStorages -> {
                var storageIds = serviceCaller.getStorageHierarchyIds(trackStorages.getItemId());
                yield new CommandExecutionResult(storageIds, false);
            }
            
            case HelpCommand helpCommand -> {
                String helpText = helpCommand.isGeneralHelp() 
                    ? helpTextProvider.getHelpText()
                    : helpTextProvider.getCommandHelp(helpCommand.getCommand());
                yield new CommandExecutionResult(false, helpText);
            }
            
            case ExitCommand ignored -> new CommandExecutionResult(true);
            
            default -> new CommandExecutionResult(false, "Unknown command. Please try again.");
        };
    }
}
