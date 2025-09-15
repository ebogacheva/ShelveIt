package org.bogacheva.training;

import org.bogacheva.training.service.dto.ItemDTO;
import org.bogacheva.training.service.dto.StorageDTO;
import org.bogacheva.training.service.item.search.ItemSearchService;
import org.bogacheva.training.service.item.crud.ItemService;
import org.bogacheva.training.service.storage.StorageService;
import org.bogacheva.training.translation.StringToCommandTranslator;
import org.bogacheva.training.translation.Translator;
import org.bogacheva.training.view.cli.ShelveItView;
import org.bogacheva.training.view.cli.commands.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CommandLineRunner implementation for the ShelveIt application.
 * Handles reading user input, translating it to commands, and executing them
 * using the service layer. Supports exit command to stop the application.
 */
@Component
public class ShelveItCommandLineRunner implements CommandLineRunner {

    private final ShelveItView shelveItView;
    private final Translator<String, BaseCommand> translator;
    private final ItemService itemService;
    private final StorageService storageService;
    private final ItemSearchService itemSearchService;

    public ShelveItCommandLineRunner(
            ShelveItView shelveItView,
            StringToCommandTranslator translator,
            ItemService itemService,
            StorageService storageService,
            ItemSearchService itemSearchService) {
        this.shelveItView = shelveItView;
        this.translator = translator;
        this.itemService = itemService;
        this.storageService = storageService;
        this.itemSearchService = itemSearchService;
    }

    @Override
    public void run(String... args) {
        shelveItView.printHeader();
        boolean exitRequested = false;
        do {
            shelveItView.printPrompt();
            try {
                String userInput = shelveItView.readCommand();
                BaseCommand command = translator.translate(userInput);
                exitRequested = handleCommand(command);
            } catch (Exception e) {
                shelveItView.printError(e.getMessage());
            }
        } while (!exitRequested);
        shelveItView.printExit();
    }

    private boolean handleCommand(BaseCommand cmd) {
        if (cmd instanceof BrokenCommand brokenCommand) {
            shelveItView.printError("Broken command: " + brokenCommand.getErrorMessage());
            return false;
        }
        switch (cmd) {
            case CreateItemCommand createItem -> {
                ItemDTO newItem = itemService.create(createItem.getCreateItemDTO());
                System.out.println("Created item: " + "\n" + newItem);
            }

            case CreateStorageCommand createStorage -> {
                StorageDTO newStorage = storageService.create(createStorage.getStorageCreateDTO());
                System.out.println("Created storage: " + "\n" + newStorage);
            }

            case ListItemsCommand listItem -> {
                var items = itemService.getAll();
                shelveItView.print(items);
            }

            case ListStoragesCommand listStorage -> {
                var storages = storageService.getAll(null);
                shelveItView.print(storages);
            }

            case RemoveItemCommand removeItem ->
                    itemService.delete(removeItem.getId());

            case RemoveStorageCommand removeStorage ->
                    storageService.delete(removeStorage.getId());

            case GetItemsByStorageCommand getItemsByStorage -> {
                var items = itemSearchService.getByStorageId(getItemsByStorage.getStorageId());
                shelveItView.print(items);
            }

            case ListSubStoragesCommand listSubStorages -> {
                var subStorages = storageService.getSubStorages(listSubStorages.getStorageId());
                shelveItView.print(subStorages);
            }

            case GetItemByIdCommand getItemById -> {
                var item = itemService.getById(getItemById.getId());
                shelveItView.print(List.of(item));
            }
            case GetStorageByIdCommand getStorageById -> {
                var storage = storageService.getById(getStorageById.getId());
                shelveItView.print(List.of(storage));
            }

            case SearchItemCommand searchItem -> {
                var results = itemSearchService.search(searchItem.getName(), searchItem.getKeywords());
                shelveItView.print(results);
            }

            case GetItemsNearCommand getItemsNear -> {
                var items = itemSearchService.getItemsNear(getItemsNear.getItemId());
                shelveItView.print(items);
            }

            case TrackStoragesHierarchyCommand trackStorages -> {
                var storageIds = itemSearchService.getStorageHierarchyIds(trackStorages.getItemId());
                shelveItView.print(storageIds);
            }

            case ExitCommand ignored -> {
                return true;
            }

            default -> {
                shelveItView.printError("Unknown command. Please try again.");
            }
        }
        return false;
    }
}
