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
import org.redhat.tme.entities.Event;
import org.redhat.tme.services.EventsService;

import java.net.URI;
import java.util.Set;
import java.util.UUID;



@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {
    private static final Logger LOGGER = Logger.getLogger(EventResource.class.getName());

    @Inject
    EventsService service;

    @GET
    public Response getAll() {
        Set<Event> events = service.getAllEvents();

        return Response.ok(events)
                .status(Response.Status.OK)
                .build();
    }

    @GET
    @Path("{id}")
    public Response getById(UUID id) {
        Event event = service.getEventById(id);

        return Response.ok(event)
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Transactional
    public Response create(Event newEvent) {
        Event event = service.updateOrInsert(newEvent);

        return Response.created(URI.create("/events/" + event.getId()))
                .status(Response.Status.CREATED)
                .build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response update(UUID id, Event eventToUpdate) {
        Event event = service.updateOrInsert(eventToUpdate);

        return Response.accepted(event)
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
