package org.redhat.tme.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.redhat.tme.entities.Event;

import java.util.List;
import java.util.UUID;



@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {
    private static final Logger LOGGER = Logger.getLogger(EventResource.class.getName());

    @GET
    public Uni<List<Event>> getAll() {
        return Event.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Uni<Event> getById(UUID id) {
        return Event.findById(id);
    }

    @POST
    public Uni<Response> create(Event newEvent) {
        if (newEvent == null )
            throw new WebApplicationException("Event payload is required");
        return Panache
                .withTransaction(newEvent::persist)
                .replaceWith(Response.ok(newEvent).status(Response.Status.CREATED)::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(UUID id, Event event) {
        if (event == null)
                throw new WebApplicationException("Event payload is required to update");
        return Panache
                .withTransaction(() -> Event.<Event> findById(id)
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(Response.Status.NOT_FOUND)::build));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(UUID id) {
        return Panache
                .withTransaction(() -> Event.deleteById(id))
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
