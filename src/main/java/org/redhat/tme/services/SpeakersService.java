package org.redhat.tme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.redhat.tme.entities.Speaker;
import org.redhat.tme.repositories.SpeakersRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SpeakersService {

    @Inject
    SpeakersRepository repository;

    public Speaker getSpeakerById(UUID speakerId) {
        return repository.findSpeakerById(speakerId);
    }

    public List<Speaker> getSpeakersForEvent(UUID eventId) {
        return repository.getAllSpeakersForEvent(eventId);
    }

    public Speaker updateOrInsert(Speaker entity) {
        return repository.updateOrInsert(entity);
    }
}