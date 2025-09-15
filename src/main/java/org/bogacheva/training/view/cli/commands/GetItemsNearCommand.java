package org.bogacheva.training.view.cli.commands;

public class GetItemsNearCommand extends BaseCommand {
    private final Long itemId;

    public GetItemsNearCommand(Long itemId) {
        super(CommandType.GET_ITEMS_NEAR);
        this.itemId = itemId;
    }

    public Long getItemId() {
        return itemId;
    }
}
