package com.smartspend.backend.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        System.out.println("🔥 Firebase init started");

        try {
            String firebaseJson = System.getenv("FIREBASE_SERVICE_ACCOUNT");

            System.out.println("Env exists: " + (firebaseJson != null));

            if (firebaseJson == null) {
                throw new RuntimeException("Missing env var");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8))
                    ))
                    .build();

            FirebaseApp.initializeApp(options);

            System.out.println("🔥 Firebase INIT SUCCESS");

        } catch (Exception e) {
            System.out.println("🔥 Firebase INIT FAILED");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    }
