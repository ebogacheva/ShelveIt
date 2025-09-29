package org.bogacheva.training.view.cli.commands;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class BrokenCommand extends BaseCommand {

    private final String errorMessage;

    public BrokenCommand(String errorMessage) {

        super(CommandType.BROKEN);
        this.errorMessage = errorMessage;
    }
}
