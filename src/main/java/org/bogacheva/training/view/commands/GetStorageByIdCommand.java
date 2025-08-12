package org.bogacheva.training.view.commands;

public class GetStorageByIdCommand extends BaseCommand {
    private final long id;

    public GetStorageByIdCommand(long id) {
        super(CommandType.GET_STORAGE);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
