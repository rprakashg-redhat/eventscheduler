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
import org.redhat.tme.entities.Session;
import org.redhat.tme.services.SessionsService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SessionResource {

    private static final Logger LOGGER = Logger.getLogger(SessionResource.class.getName());

    @Inject
    SessionsService service;

    @GET
    public Response getSessions(@QueryParam("eventId") UUID eventId) {
        List<Session> sessions = service.getSessionsForEvent(eventId);
        return Response.ok(sessions)
                .status(Response.Status.OK)
                .build();
    }

    @GET
    @Path("/{sessionId}")
    public Response getSession(@PathParam("sessionId") UUID sessionId, @QueryParam("eventId") UUID eventId) {
        Session session = service.getSessionById(sessionId);

        return Response.ok(session)
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Transactional
    public Response newSession(Session newSession) {
        Session session = service.updateOrInsert(newSession);

        return Response.created(URI.create( "/sessions/" + session.getId()))
                .status(Response.Status.CREATED)
                .build();
    }

    @PUT
    @Path("/{sessionId}")
    @Transactional
    public Response updateSession(@PathParam("sessionId") UUID sessionId, Session sessionToUpdate) {
        Session session = service.updateOrInsert(sessionToUpdate);

        return Response.accepted(session)
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
