package org.bogacheva.training.view.cli.commands;

import lombok.Getter;

@Getter
public class TrackStoragesHierarchyCommand extends BaseCommand {
    private final Long itemId;

    public TrackStoragesHierarchyCommand(Long itemId) {
        super(CommandType.TRACK_STORAGES);
        this.itemId = itemId;
    }
}
