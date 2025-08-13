package org.bogacheva.training.view.commands;


public class SearchItemsByKeywordsCommand extends BaseCommand {
    private final String keyword;

    public SearchItemsByKeywordsCommand(String keyword) {
        super(CommandType.SEARCH_ITEMS_BY_KEYWORD);
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
