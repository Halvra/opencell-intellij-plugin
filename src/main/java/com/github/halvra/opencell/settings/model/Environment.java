package com.github.halvra.opencell.settings.model;

import javax.xml.bind.annotation.XmlTransient;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class Environment {
    private String name;
    private String url;
    private String authorization;
    @XmlTransient
    private String username;
    @XmlTransient
    private String password;
    private boolean preferred;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

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

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
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

    @Override
    public String toString() {
        return "Environment{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", authorization='" + authorization + '\'' +
                ", preferred=" + preferred +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Environment that = (Environment) o;
        return preferred == that.preferred && Objects.equals(name, that.name) && Objects.equals(url, that.url) && Objects.equals(authorization, that.authorization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, authorization, preferred);
    }
}
