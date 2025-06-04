package org.bogacheva.training.domain.storage;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.bogacheva.training.domain.item.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "storages")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private StorageType type;

    @OneToMany(mappedBy = "storage", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) //TODO: need LAZY
    private List<Item> items;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) //TODO: need LAZY
    private List<Storage> subStorages;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Storage parent;

    public Storage(String name, StorageType type, Storage parent) {
        this.name = name;
        this.type = type;
        this.items = new ArrayList<>();
        this.subStorages = new ArrayList<>();
        this.parent = parent;
    }
}
