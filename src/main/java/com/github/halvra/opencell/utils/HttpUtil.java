package com.github.halvra.opencell.utils;

import com.intellij.util.net.HttpConfigurable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtil {
    private static void configureProxy(String url, Consumer<Proxy> proxyConsumer, BiConsumer<String, String> credentialsConsumer) {
        HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
        if (!httpConfigurable.isHttpProxyEnabledForUrl(url)) {
            return;
        }
        Proxy.Type type = httpConfigurable.PROXY_TYPE_IS_SOCKS ? Proxy.Type.SOCKS : Proxy.Type.HTTP;

        Proxy proxy = new Proxy(type, new InetSocketAddress(httpConfigurable.PROXY_HOST, httpConfigurable.PROXY_PORT));
        proxyConsumer.accept(proxy);

        if (httpConfigurable.PROXY_AUTHENTICATION) {
            String proxyLogin = httpConfigurable.getProxyLogin();
            if (proxyLogin != null) {
                credentialsConsumer.accept(proxyLogin, httpConfigurable.getPlainProxyPassword());
            }
        }
    }
}
