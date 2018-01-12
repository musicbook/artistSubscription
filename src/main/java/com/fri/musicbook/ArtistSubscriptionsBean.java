package com.fri.musicbook;

import com.kumuluz.ee.fault.tolerance.annotations.*;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Bulkhead
@GroupKey("artistsubscription")
public class ArtistSubscriptionsBean {
    @PersistenceContext(unitName = "artistsubscriptions-jpa")
    private EntityManager em;

    @CircuitBreaker
    @Fallback(fallbackMethod = "findArtistSubscriptionsFallback")
    @CommandKey("http-get-artistsubs")
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    public List<ArtistSubscription> getArtistSubscriptions(){
        Query query = em.createNamedQuery("ArtistSubscription.getAll", ArtistSubscription.class);
        return query.getResultList();
    }

    @Log
    public List<ArtistSubscription> findArtistSubscriptionsFallback() {

        //log.info("Fallback called for findOrdersByCustomerId.");

        ArtistSubscription as = new ArtistSubscription();
        as.setId("-1");
        as.setId_artist("-1");
        as.setId_user("-1");

        List<ArtistSubscription> subs = new ArrayList<>();
        subs.add(as);

        return subs;
    }

    public ArtistSubscription getArtistSubscription(String gsId) {
        ArtistSubscription gs = em.find(ArtistSubscription.class, gsId);
        if (gs == null) {
            throw new NotFoundException();
        }
        return gs;
    }

    @CircuitBreaker
    @Fallback(fallbackMethod = "findArtistSubscriptionsFallback")
    @CommandKey("http-get-artistsubs-filtered")
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    public List<ArtistSubscription> getArtistSubscriptionsFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();
        List<ArtistSubscription> gs = JPAUtils.queryEntities(em, ArtistSubscription.class, queryParameters);
        return gs;
    }

    public ArtistSubscription createArtist(ArtistSubscription gs) {
        try {
            beginTx();
            em.persist(gs);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }
        return gs;
    }

    public ArtistSubscription putArtistSubscription(String gsId, ArtistSubscription artist) {

        ArtistSubscription c = em.find(ArtistSubscription.class, gsId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            artist.setId(c.getId());
            artist = em.merge(artist);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return artist;
    }

    public boolean deleteArtistSubscription(String gsId) {

        ArtistSubscription gs = em.find(ArtistSubscription.class, gsId);

        if (gs != null) {
            try {
                beginTx();
                em.remove(gs);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
