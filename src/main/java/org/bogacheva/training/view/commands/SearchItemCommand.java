package org.bogacheva.training.view.commands;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SearchItemCommand extends BaseCommand {
    private final String name;
    private final List<String> keywords;

    public SearchItemCommand(String name, List<String> keywords) {
        super(CommandType.SEARCH_ITEM);
        this.name = name;
        this.keywords = keywords;
    }
}
