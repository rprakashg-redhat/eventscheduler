package org.redhat.tme.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.redhat.tme.enums.EventType;
import org.redhat.tme.utils.PostgreSqlStringArrayType;


import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
@Entity
public class Event extends PanacheEntityBase {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "event_id", updatable = false, nullable = false)
    @Getter
    @Setter
    private UUID id;

    @Column(name = "event_name", nullable = false, unique = true)
    @Getter
    @Setter
    private String name;

    @Column(name = "event_description", nullable = false, columnDefinition = "TEXT")
    @Getter
    @Setter
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    @Getter
    @Setter
    private EventType type;

    @Column(name = "event_location", nullable = false)
    @Getter
    @Setter
    private String location;

    @Column(columnDefinition = "text[]")
    @Getter
    @Setter
    @Type(value = PostgreSqlStringArrayType.class)
    private String[] audience;

    @Column(columnDefinition = "text[]")
    @Getter
    @Setter
    @Type(value = PostgreSqlStringArrayType.class)
    private String[] topics;
}
