package org.redhat.tme.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.redhat.tme.entities.Event;

import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class EventsRepository implements PanacheRepository<Event> {
    public Event findByEventId(UUID eventId) {
        return find("id", eventId).firstResult();
    }
    public Stream<Event> getAllEvents() {
        return findAll().stream();
    }
    public Event updateOrInsert(Event entity) {
        Event fromDb = findByEventId(entity.getId());
        if (fromDb != null) {
            fromDb.setAudiences(entity.getAudiences());
            fromDb.setDescription(entity.getDescription());
            fromDb.setLocation(entity.getLocation());
            fromDb.setName(entity.getName());
            fromDb.setTopics(entity.getTopics());
            fromDb.setType(entity.getType());
            persist(fromDb);
            return fromDb;
        }
        persist(entity);
        return entity;
    }
}