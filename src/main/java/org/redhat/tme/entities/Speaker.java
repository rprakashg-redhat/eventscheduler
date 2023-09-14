package org.redhat.tme.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "speakers")
@Entity
@NamedQueries({
        @NamedQuery(
            name = "Speaker.findByEvent",
            query = "SELECT DISTINCT s " +
                    "FROM Speaker s "  +
                    "JOIN Session se " +
                    "JOIN Event e " +
                    "WHERE s.id = se.speaker " +
                    "AND se.event = e.id " +
                    "AND e.id = :eventId"
        )
})
public class Speaker extends PanacheEntityBase {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "uuid2",
            type = org.hibernate.id.uuid.UuidGenerator.class
    )
    @Column(name = "speaker_id", updatable = false, nullable = false)
    @Getter
    @Setter
    private UUID id;

    @Column(name = "speaker_name", nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(name = "speaker_title", nullable = false)
    @Getter
    @Setter
    private String title;

    @Column(name = "company_name", nullable = false)
    @Getter
    @Setter
    private String company;

    @Column(name = "email_address", nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(name = "twitter")
    @Getter
    @Setter
    private String twitter;

    @Column(name = "linkedin")
    @Getter
    @Setter
    private String linkedIn;
}
