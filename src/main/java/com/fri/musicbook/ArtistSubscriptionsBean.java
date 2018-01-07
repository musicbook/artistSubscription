package com.fri.musicbook;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@RequestScoped
public class ArtistSubscriptionsBean {
    @PersistenceContext(unitName = "artistsubscriptions-jpa")
    private EntityManager em;

    public List<ArtistSubscription> getArtistSubscriptions(){
        Query query = em.createNamedQuery("ArtistSubscription.getAll", ArtistSubscription.class);
        return query.getResultList();
    }

    public ArtistSubscription getArtistSubscription(String gsId) {
        ArtistSubscription gs = em.find(ArtistSubscription.class, gsId);
        if (gs == null) {
            throw new NotFoundException();
        }
        return gs;
    }

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
