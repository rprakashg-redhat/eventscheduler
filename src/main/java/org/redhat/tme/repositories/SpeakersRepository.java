package org.redhat.tme.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.redhat.tme.entities.Speaker;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class SpeakersRepository implements PanacheRepository<Speaker> {
    public Speaker findSpeakerById(UUID speakerId) {
        return find("id", speakerId).firstResult();
    }
    public List<Speaker> getAllSpeakersForEvent(UUID eventId){
        return Speaker.find("#Speaker.findByEvent", eventId).list();
    }
    public Speaker updateOrInsert(Speaker entity) {
        Speaker fromDb = findSpeakerById(entity.getId());
        if (fromDb != null) {
            fromDb.setCompany(entity.getCompany());
            fromDb.setEmail(entity.getEmail());
            fromDb.setLinkedIn(entity.getLinkedIn());
            fromDb.setName(entity.getName());
            fromDb.setTitle(entity.getTitle());
            fromDb.setTwitter(entity.getTwitter());

            persist(fromDb);
            return fromDb;
        }
        persist(entity);
        return entity;
    }
}