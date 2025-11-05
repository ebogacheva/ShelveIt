package org.bogacheva.training.service.command;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class GetStorageByIdCommand extends BaseCommand {
    private final Long id;

    public GetStorageByIdCommand(long id) {
        super(CommandType.GET_STORAGE);
        this.id = id;
    }
}
