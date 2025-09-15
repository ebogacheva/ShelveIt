package org.bogacheva.training.view.cli.commands;

public class GetStorageByIdCommand extends BaseCommand {
    private final Long id;

    public GetStorageByIdCommand(long id) {
        super(CommandType.GET_STORAGE);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
