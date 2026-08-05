package com.staysmart.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Type-safe binding for the {@code app.*} configuration tree in application.yml
 * (JWT secrets/expirations, CORS, upload storage, AI provider settings, booking policy).
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Cors cors = new Cors();
    private final Uploads uploads = new Uploads();
    private final Ai ai = new Ai();
    private final Booking booking = new Booking();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long accessTokenExpirationMs;
        private long refreshTokenExpirationMs;
    }

    @Getter
    @Setter
    public static class Cors {
        private String allowedOrigins;
    }

    @Getter
    @Setter
    public static class Uploads {
        private String dir;
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class Ai {
        private final OpenAi openai = new OpenAi();

        @Getter
        @Setter
        public static class OpenAi {
            private String apiKey;
            private String chatModel;
            private double temperature;
            private int timeoutSeconds;
        }
    }

    @Getter
    @Setter
    public static class Booking {
        private int freeCancellationHours;
    }
}
