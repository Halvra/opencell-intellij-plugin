package com.github.halvra.opencell.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.dto.ScriptInstanceDto;
import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class DeployScriptToEnvironmentTask extends Task.Backgroundable {
    private static final Logger LOGGER = Logger.getInstance(DeployScriptToEnvironmentTask.class);

    private final ScriptInstanceDto scriptInstance;
    private final Environment environment;

    public DeployScriptToEnvironmentTask(@Nullable Project project, @NotNull ScriptInstanceDto scriptInstance, @NotNull Environment environment) {
        super(project, OpencellBundle.message("tasks.deployScript.title"));

        this.scriptInstance = scriptInstance;
        this.environment = environment;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        indicator.setText(OpencellBundle.message("tasks.deployScript.progress", scriptInstance.getCode(), environment.getName()));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(environment.getUrl() + "/api/rest/scriptInstance/createOrUpdate");
        httpPost.setHeader("Authorization", "Basic " + environment.getAuthorization());
        try {
            httpPost.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(scriptInstance), ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOGGER.warn("Failed to deploy script to target environment: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to deploy script to target environment", e);
        }
    }
}
