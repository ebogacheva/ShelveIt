package org.bogacheva.training.repository.item;

import org.bogacheva.training.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
