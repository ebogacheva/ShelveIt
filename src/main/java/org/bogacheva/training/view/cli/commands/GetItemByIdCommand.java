package org.bogacheva.training.view.cli.commands;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class GetItemByIdCommand extends BaseCommand {
    private final Long id;

    public GetItemByIdCommand(long id) {
        super(CommandType.GET_ITEM);
        this.id = id;
    }
}
