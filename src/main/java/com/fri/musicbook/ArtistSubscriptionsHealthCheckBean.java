package com.fri.musicbook;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import java.net.HttpURLConnection;
import java.net.URL;

@Health
@ApplicationScoped
public class ArtistSubscriptionsHealthCheckBean implements HealthCheck{

    /*
    @Inject
    @DiscoverService("artists")
    private Optional<String> url;
    */
    private static final String url = "http://localhost:8085/v1/artistSubscription";

    @Override
    public HealthCheckResponse call() {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");

            if (connection.getResponseCode() == 200) {
                return HealthCheckResponse.named(ArtistSubscriptionsHealthCheckBean.class.getSimpleName()).up().build();
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return HealthCheckResponse.named(ArtistSubscriptionsHealthCheckBean.class.getSimpleName()).down().build();
    }
}
