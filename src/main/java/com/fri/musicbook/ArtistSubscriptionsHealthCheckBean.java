package com.fri.musicbook;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.net.URL;

@Health
@ApplicationScoped
public class ArtistSubscriptionsHealthCheckBean implements HealthCheck{

    @Inject
    @DiscoverService("artist-service")
    private String basePath;
    private static final String url = "http://localhost:8085/v1/artistSubscription";

    @Override
    public HealthCheckResponse call() {
        try {
            System.out.println(basePath);
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
