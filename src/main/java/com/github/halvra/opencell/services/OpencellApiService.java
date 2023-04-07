package com.github.halvra.opencell.services;

import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.utils.HttpUtil;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;

import java.io.IOException;

public class OpencellApiService {
    private final Gson gson;
    private final Environment environment;

    private OpencellApiService(Environment environment) {
        this.gson = new Gson();
        this.environment = environment;
    }

    public static OpencellApiService getInstance(Environment environment) {
        return new OpencellApiService(environment);
    }

    public GetScriptInstanceResponseDto getScript(String qualifiedName) throws IOException {
        return get("/api/rest/scriptInstance?scriptInstanceCode=" + qualifiedName, GetScriptInstanceResponseDto.class);
    }

    public ScriptInstanceReponseDto createOrUpdateScript(ScriptInstanceDto scriptInstanceDto) throws IOException {
        return post("/api/rest/scriptInstance/createOrUpdate", scriptInstanceDto, ScriptInstanceReponseDto.class);
    }

    private <T> T get(String path, Class<T> returnType) throws IOException {
        HttpGet httpGet = new HttpGet(environment.getUrl() + path);
        return execute(httpGet, returnType);
    }

    private <T> T post(String path, Object body, Class<T> returnType) throws IOException {
        HttpPost httpPost = new HttpPost(environment.getUrl() + path);
        httpPost.setEntity(new StringEntity(gson.toJson(body), ContentType.APPLICATION_JSON));

        return execute(httpPost, returnType);
    }

    private <T> T execute(HttpUriRequest request, Class<T> returnType) throws IOException {
        request.setHeader("Authorization", "Basic " + environment.getAuthorization());

        try (CloseableHttpClient client = getClient()) {
            ResponseHandler<T> responseHandler = response -> {
                HttpEntity entity = response.getEntity();
                return entity != null ? gson.fromJson(EntityUtils.toString(entity), returnType) : null;
            };

            return client.execute(request, responseHandler);
        }
    }

    private CloseableHttpClient getClient() {
        final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        HttpUtil.configureProxy(environment.getUrl(), clientBuilder::setProxy, credentialsProvider::setCredentials);

        return clientBuilder.build();
    }
}
