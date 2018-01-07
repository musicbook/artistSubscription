package com.fri.musicbook;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

@Entity(name = "artistsubscription")
@NamedQueries(value =
        {
                @NamedQuery(name = "ArtistSubscription.getAll", query = "SELECT c FROM artistsubscription c")
        })
@UuidGenerator(name = "idGenerator")
public class ArtistSubscription {
        @Id
        @GeneratedValue(generator = "idGenerator")
        private String id;

        @Column(name = "id_user")
        private String id_user;

        @Column(name = "id_artist")
        private String id_artist;


        public String getId_artist() {
                return id_artist;
        }

        public void setId_artist(String id_artist) {
                this.id_artist = id_artist;
        }

        public String getId_user() {
                return id_user;
        }

        public void setId_user(String id_user) {
                this.id_user = id_user;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

}
