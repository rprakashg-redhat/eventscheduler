package org.redhat.tme.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import org.redhat.tme.entities.Session;
import org.redhat.tme.entities.Speaker;

import java.util.List;
import java.util.UUID;

@Path("/speakers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpeakerResource {

    private static final Logger LOGGER = Logger.getLogger(SpeakerResource.class.getName());

    @GET
    public Uni<List<Speaker>> getSpeakers(@QueryParam("eventId") UUID eventId) {
        return Speaker.find("#Speaker.findByEvent", eventId).list();
    }

    @GET
    @Path("{id}")
    public Uni<Speaker> getById(UUID id) {
        return Speaker.findById(id);
    }

    @POST
    public Uni<Response> create(Speaker newSpeaker) {
        if (newSpeaker == null)
            throw new WebApplicationException("Speaker payload is missing");

        return Panache
                .withTransaction(newSpeaker::persist)
                .replaceWith(Response.ok(newSpeaker).status(Response.Status.CREATED)::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(UUID id, Speaker speaker) {
        if (speaker == null)
            throw new WebApplicationException("Speaker payload is missing");
        return Panache
                .withTransaction(() -> Speaker.<Speaker> findById(id)
                        .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                        .onItem().ifNull().continueWith(Response.ok().status(Response.Status.NOT_FOUND)::build));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(UUID id) {
        return Panache
                .withTransaction(() -> Speaker.deleteById(id))
                .map(deleted -> deleted ? Response.ok().status(Response.Status.NO_CONTENT).build()
                        : Response.ok().status(Response.Status.NOT_FOUND).build());
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
