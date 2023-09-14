package org.redhat.tme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.redhat.tme.entities.Session;
import org.redhat.tme.repositories.SessionsRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SessionsService {
    @Inject
    SessionsRepository repository;

    public List<Session> getSessionsForEvent(UUID eventId) {
        return repository.getAllSessionsForEvent(eventId);
    }

    public Session getSessionById(UUID id) {
        return repository.findSessionBySessionId(id);
    }

    public Session updateOrInsert(Session entity) {
        return repository.updateOrInsert(entity);
    }
}