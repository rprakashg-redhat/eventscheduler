package org.redhat.tme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.redhat.tme.entities.Event;
import org.redhat.tme.repositories.EventsRepository;

import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@ApplicationScoped
public class EventsService {

    @Inject
    EventsRepository repository;

    public Set<Event> getAllEvents() {
        return repository.getAllEvents().collect(toSet());
    }

    public Event getEventById(UUID eventId) {
        return repository.findByEventId(eventId);
    }

    public Event updateOrInsert(Event entity) {
        return repository.updateOrInsert(entity);
    }
}