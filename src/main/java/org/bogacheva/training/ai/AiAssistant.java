package org.bogacheva.training.ai;

import org.bogacheva.training.service.dto.ItemDTO;

import java.util.List;

/**
 * Boundary: input is plain text, output is a structured result.
 * Implementations must not access repositories directly — use service layer interfaces only.
 */
public interface AiAssistant {

    /**
     * Find items semantically matching the natural language query.
     */
    List<ItemDTO> findItems(String query);
}