package org.bogacheva.training.domain.storage;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bogacheva.training.domain.item.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private StorageType type;

    @OneToMany(mappedBy = "storage")
    private List<Item> items;

    @OneToMany(mappedBy = "parent")
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
