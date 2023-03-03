package com.github.halvra.opencell.settings.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlTransient;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Data
public class Environment {
    private String name;
    private String url;
    private String authorization;
    @XmlTransient
    private String username;
    @XmlTransient
    private String password;
    private boolean preferred;

    public void setAuthorization(String username, String password) {
        this.username = username;
        this.password = password;

        String decodedAuthorization = username + ":" + password;
        this.authorization = Base64.getEncoder().encodeToString(decodedAuthorization.getBytes(StandardCharsets.UTF_8));
    }

    public String getUsername() {
        if (username == null || username.trim().isEmpty()) {
            decodeAuthorization();
        }
        return username;
    }

    public String getPassword() {
        if (password == null || password.trim().isEmpty()) {
            decodeAuthorization();
        }
        return password;
    }

    private void decodeAuthorization() {
        if (this.authorization != null && !this.authorization.trim().isEmpty()) {
            String decodedAuthorization = new String(Base64.getDecoder().decode(this.authorization));
            if (decodedAuthorization.contains(":")) {
                String[] userPass = decodedAuthorization.split(":");
                this.username = userPass[0];
                this.password = userPass[1];
            }
        }
    }
}
