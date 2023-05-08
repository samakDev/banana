package org.samak.banana.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "plush")
public class PlushEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ClawMachineEntity clawMachine;

    private String name;

    private String imageAbsolutePath;

    @Column(name = "number")
    private int order;

    @Enumerated(EnumType.STRING)
    private PlushStateEnumEntity state;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public ClawMachineEntity getClawMachine() {
        return clawMachine;
    }

    public void setClawMachineId(final ClawMachineEntity clawMachine) {
        this.clawMachine = clawMachine;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getImageAbsolutePath() {
        return imageAbsolutePath;
    }

    public void setImageAbsolutePath(final String imageAbsolutPath) {
        this.imageAbsolutePath = imageAbsolutPath;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public PlushStateEnumEntity getState() {
        return state;
    }

    public void setState(final PlushStateEnumEntity state) {
        this.state = state;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PlushEntity entity = (PlushEntity) o;
        return order == entity.order && Objects.equals(id, entity.id) && Objects.equals(clawMachine, entity.clawMachine) && Objects.equals(name, entity.name) && Objects.equals(imageAbsolutePath, entity.imageAbsolutePath) && state == entity.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clawMachine, name, imageAbsolutePath, order, state);
    }

    @Override
    public String toString() {
        return "PlushEntity{" +
                "id=" + id +
                ", clawMachine=" + clawMachine +
                ", name='" + name + '\'' +
                ", imageAbsolutePath='" + imageAbsolutePath + '\'' +
                ", order=" + order +
                ", state=" + state +
                '}';
    }
}
