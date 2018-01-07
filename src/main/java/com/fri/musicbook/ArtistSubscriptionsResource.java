package com.fri.musicbook;

import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@RequestScoped
@Path("/artistSubscription")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistSubscriptionsResource {
    @Context
    protected UriInfo uriInfo;

    @Inject
    private ArtistSubscriptionsBean gsb;

    @Metered(name = "getArtistSubscriptions")
    @GET
    public Response getArtistSubscriptions() {
        System.out.println("getVsiGen");
        List<ArtistSubscription> subs = gsb.getArtistSubscriptions();

        return Response.ok(subs).build();
    }

    @Metered(name = "getArtistSubscription")
    @GET
    @Path("/{gsId}")
    public Response getArtist(@PathParam("gsId") String gsId) {
        System.out.println("get1Gen");
        ArtistSubscription sub = gsb.getArtistSubscription(gsId);

        if (sub == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(sub).build();
    }

    @Timed(name = "getArtistSubscriptionsFiltered_time")
    @Metered(name = "getArtistSubscriptionsFiltered")
    @GET
    @Path("/filtered")
    public Response getArtistSubscriptionsFiltered() {

        List<ArtistSubscription> artists;

        artists = gsb.getArtistSubscriptionsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(artists).build();
    }

    @Metered(name = "createArtistSubscription")
    @POST
    public Response createArtist(ArtistSubscription gs) {

        if ((gs.getId_artist() == null || gs.getId_artist().isEmpty() || gs.getId_user() == null || gs.getId_user().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            gs = gsb.createArtist(gs);
        }

        if (gs.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(gs).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(gs).build();
        }
    }

    @Metered(name = "putArtistSubscription")
    @PUT
    @Path("{gsId}")
    public Response putArtistSubscription(@PathParam("gsId") String gsId, ArtistSubscription gs) {

        gs = gsb.putArtistSubscription(gsId, gs);

        if (gs == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (gs.getId() != null)
                return Response.status(Response.Status.OK).entity(gs).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @Metered(name = "deleteArtist")
    @DELETE
    @Path("{artistId}")
    public Response deleteArtist(@PathParam("artistId") String artistId) {

        boolean deleted = gsb.deleteArtistSubscription(artistId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
