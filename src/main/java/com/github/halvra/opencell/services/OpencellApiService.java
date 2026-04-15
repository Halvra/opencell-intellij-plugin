package com.github.halvra.opencell.services;

import com.github.halvra.opencell.settings.model.Environment;
import com.google.gson.Gson;
import com.intellij.util.io.HttpRequests;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
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
        return HttpRequests
                .request(environment.getUrl() + path)
                .tuner(connection -> connection.setRequestProperty("Authorization", "Basic " + environment.getAuthorization()))
                .connect(request -> readResponse(request, returnType));
    }

    private <T> T post(String path, Object body, Class<T> returnType) throws IOException {
        HttpPost httpPost = new HttpPost(environment.getUrl() + path);
        httpPost.setEntity(new StringEntity(gson.toJson(body), ContentType.APPLICATION_JSON));

        return HttpRequests
                .post(environment.getUrl() + path, "application/json")
                .tuner(connection -> connection.setRequestProperty("Authorization", "Basic " + environment.getAuthorization()))
                .connect(request -> {
                    request.write(gson.toJson(body));
                    return readResponse(request, returnType);
                });
    }

    private <T> T readResponse(HttpRequests.Request request, Class<T> returnType) throws IOException {
        return gson.fromJson(request.readString(), returnType);
    }
}
