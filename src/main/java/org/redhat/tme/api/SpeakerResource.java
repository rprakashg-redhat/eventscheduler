package org.redhat.tme.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.smallrye.mutiny.CompositeException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import org.redhat.tme.entities.Speaker;
import org.redhat.tme.services.SpeakersService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/speakers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpeakerResource {

    private static final Logger LOGGER = Logger.getLogger(SpeakerResource.class.getName());

    @Inject
    SpeakersService service;

    @GET
    public Response getSpeakers(@QueryParam("eventId") UUID eventId) {
        List<Speaker> speakers = service.getSpeakersForEvent(eventId);
        return Response.ok(speakers)
                .status(Response.Status.OK)
                .build();
    }

    @GET
    @Path("{speakerId}")
    public Response getSpeaker(@PathParam("speakerId") UUID speakerId) {
        Speaker speaker = service.getSpeakerById(speakerId);

        return Response.ok(speaker)
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Transactional
    public Response newSpeaker(Speaker newSpeaker) {
        Speaker speaker = service.updateOrInsert(newSpeaker);

        return Response.created(URI.create("/speakers/" + speaker.getId()))
                .status(Response.Status.CREATED)
                .build();
    }

    @PUT
    @Path("{speakerId}")
    @Transactional
    public Response updateSpeaker(@PathParam("speakerId") UUID speakerId, Speaker speakerToUpdate) {
        Speaker speaker = service.updateOrInsert(speakerToUpdate);

        return Response.accepted(speaker)
                .status(Response.Status.ACCEPTED)
                .build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            Throwable throwable = exception;

            int code = 500;
            if (throwable instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            // This is a Mutiny exception and it happens, for example, when we try to insert a new
            // fruit but the name is already in the database
            if (throwable instanceof CompositeException) {
                throwable = ((CompositeException) throwable).getCause();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", throwable.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", throwable.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }
}
