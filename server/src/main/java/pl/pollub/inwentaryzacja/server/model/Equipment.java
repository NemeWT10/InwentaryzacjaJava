package pl.pollub.inwentaryzacja.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Workstation workstation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentType type;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String microcontroller;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status;
}
