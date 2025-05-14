package entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class PasswordResetToken {
    private int id;
    private int clientId;
    private String token;
    private LocalDateTime expiryDate;
    private boolean used;

    // Token expiration time in hours
    private static final int EXPIRATION = 24;

    public PasswordResetToken() {
    }

    public PasswordResetToken(int clientId) {
        this.clientId = clientId;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION);
        this.used = false;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}