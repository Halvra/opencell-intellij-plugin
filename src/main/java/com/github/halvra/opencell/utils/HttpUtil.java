package com.github.halvra.opencell.utils;

import com.intellij.util.net.HttpConfigurable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utility class for Apache Http Client
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtil {
    /**
     * Proxy configurer based on IDE settings
     */
    public static void configureProxy(String url, Consumer<HttpHost> proxyConsumer, BiConsumer<AuthScope, UsernamePasswordCredentials> credentialsConsumer) {
        HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
        if (!httpConfigurable.isHttpProxyEnabledForUrl(url)) {
            return;
        }

        proxyConsumer.accept(new HttpHost(httpConfigurable.PROXY_HOST, httpConfigurable.PROXY_PORT));

        if (httpConfigurable.PROXY_AUTHENTICATION) {
            String proxyLogin = httpConfigurable.getProxyLogin();
            if (proxyLogin != null) {
                credentialsConsumer.accept(new AuthScope(httpConfigurable.PROXY_HOST, httpConfigurable.PROXY_PORT),
                        new UsernamePasswordCredentials(proxyLogin, httpConfigurable.getPlainProxyPassword()));
            }
        }
    }
}
