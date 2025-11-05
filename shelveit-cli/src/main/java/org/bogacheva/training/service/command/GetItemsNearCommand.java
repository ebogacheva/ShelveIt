package org.bogacheva.training.service.command;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class GetItemsNearCommand extends BaseCommand {
    private final Long itemId;

    public GetItemsNearCommand(Long itemId) {
        super(CommandType.GET_ITEMS_NEAR);
        this.itemId = itemId;
    }
}
