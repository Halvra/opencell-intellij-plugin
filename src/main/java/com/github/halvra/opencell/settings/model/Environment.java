package com.github.halvra.opencell.settings.model;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
public class Environment {
    private String name;
    private String url;
    private boolean preferred;

    public void updateName(String name) {
        var oldCredentials = this.name != null ? getCredentials() : null;

        // Update credentials if name is changed
        if (!name.equalsIgnoreCase(this.name) && oldCredentials != null) {
            PasswordSafe.getInstance().set(createCredentialAttributes(this.name), null);
            PasswordSafe.getInstance().set(createCredentialAttributes(name), oldCredentials);
        }

        this.name = name;
    }

    public String getUsername() {
        var credentials = getCredentials();
        if (credentials != null) {
            return credentials.getUserName();
        }

        return null;
    }

    public String getAuthorization() {
        var credentials = getCredentials();
        if (credentials != null) {
            String decodedAuthorization = credentials.getUserName() + ":" + credentials.getPasswordAsString();
            return Base64.getEncoder().encodeToString(decodedAuthorization.getBytes(StandardCharsets.UTF_8));
        }

        return null;
    }

    public boolean hasPassword() {
        var credentials = getCredentials();
        return credentials != null && StringUtils.isNotBlank(credentials.getPasswordAsString());
    }

    private Credentials getCredentials() {
        var credentialAttributes = createCredentialAttributes(this.name);
        return PasswordSafe.getInstance().get(credentialAttributes);
    }

    public void updateCredentials(String username, char[] password) {
        var credentialAttributes = createCredentialAttributes(this.name);
        var oldCredentials = getCredentials();
        Credentials newCredentials;
        if (oldCredentials != null) {
            newCredentials = new Credentials(username, password.length > 0 ? String.valueOf(password) : oldCredentials.getPasswordAsString());
        } else {
            newCredentials = new Credentials(username, password);
        }
        PasswordSafe.getInstance().set(credentialAttributes, newCredentials);
    }

    public void removeCredentials() {
        var credentialAttributes = createCredentialAttributes(this.name);
        PasswordSafe.getInstance().set(credentialAttributes, null);
    }

    private static CredentialAttributes createCredentialAttributes(String name) {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("Opencell Community Tools Environment", name)
        );
    }
}
