package org.bogacheva.training.view.commands;

public class SearchItemsCommand extends BaseCommand {
    private final String searchTerm;

    public SearchItemsCommand(String searchTerm) {
        super(CommandType.SEARCH_ITEMS);
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}
