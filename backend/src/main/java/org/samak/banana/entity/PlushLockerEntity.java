package org.samak.banana.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "plush_locker")
public class PlushLockerEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plushId")
    private PlushEntity plush;

    private String name;

    private OffsetDateTime lockDate;
    private OffsetDateTime unlockDate;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public PlushEntity getPlush() {
        return plush;
    }

    public void setPlush(final PlushEntity plush) {
        this.plush = plush;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public OffsetDateTime getLockDate() {
        return lockDate;
    }

    public void setLockDate(final OffsetDateTime lockDate) {
        this.lockDate = lockDate;
    }

    public OffsetDateTime getUnlockDate() {
        return unlockDate;
    }

    public void setUnlockDate(final OffsetDateTime unlockDate) {
        this.unlockDate = unlockDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PlushLockerEntity that = (PlushLockerEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(plush, that.plush) && Objects.equals(name, that.name) && Objects.equals(lockDate, that.lockDate) && Objects.equals(unlockDate, that.unlockDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, plush, name, lockDate, unlockDate);
    }

    @Override
    public String toString() {
        return "PlushLockerEntity{" +
                "id=" + id +
                ", plush=" + plush +
                ", name='" + name + '\'' +
                ", lockDate=" + lockDate +
                ", unlockDate=" + unlockDate +
                '}';
    }
}
