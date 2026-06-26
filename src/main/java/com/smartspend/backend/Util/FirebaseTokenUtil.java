package com.smartspend.backend.Util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class FirebaseTokenUtil {

    // Get UID from Bearer token
    public static String getUidFromToken(String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        String idToken = authHeader.replace("Bearer ", "").trim();
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return decodedToken.getUid(); // Unique Firebase UID
    }

    //  Optional: Get email from Bearer token (useful in UserController)
    public static String getEmailFromToken(String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        String idToken = authHeader.replace("Bearer ", "").trim();
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return decodedToken.getEmail(); // Firebase email (if needed)
    }
}
