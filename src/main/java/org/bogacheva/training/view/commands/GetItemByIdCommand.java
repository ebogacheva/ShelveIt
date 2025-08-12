package org.bogacheva.training.view.commands;

public class GetItemByIdCommand extends BaseCommand {
    private final long id;

    public GetItemByIdCommand(long id) {
        super(CommandType.GET_ITEM);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
