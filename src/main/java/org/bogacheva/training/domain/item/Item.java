package org.bogacheva.training.domain.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bogacheva.training.domain.storage.Storage;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_id")
    private Storage storage;

    public Item(String name, Storage storage) {
        this.name = name;
        this.storage = storage;
    }

    @Override
    public String toString() {
        return id + ": " + name + "storage: id " + storage.getId() + " " + storage.getName();
    }
}
