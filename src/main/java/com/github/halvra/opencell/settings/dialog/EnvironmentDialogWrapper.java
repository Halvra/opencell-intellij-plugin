package com.github.halvra.opencell.settings.dialog;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;
import java.util.List;

public class EnvironmentDialogWrapper extends DialogWrapper {
    private final JBTextField nameField = new JBTextField();
    private final JBTextField urlField = new JBTextField();
    private final JBTextField usernameField = new JBTextField();
    private final JBPasswordField passwordField = new JBPasswordField();
    private final JBCheckBox preferredField = new JBCheckBox(OpencellBundle.message("settings.environment.preferred"));

    private Environment environment;

    public EnvironmentDialogWrapper(boolean first) {
        super(true); // use current window as parent
        init();
        setTitle(OpencellBundle.message("settings.environment.dialog"));

        this.preferredField.setSelected(first);
        this.preferredField.setEnabled(!first);
    }

    public EnvironmentDialogWrapper(Environment environment, boolean first) {
        this(first);

        this.environment = environment;
        fillForm(environment);
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(OpencellBundle.message("settings.environment.name"), nameField)
                .addLabeledComponent(OpencellBundle.message("settings.environment.url"), urlField)
                .addTooltip(OpencellBundle.message("settings.environment.url.tooltip"))
                .addLabeledComponent(OpencellBundle.message("settings.environment.username"), usernameField)
                .addLabeledComponent(OpencellBundle.message("settings.environment.password"), passwordField)
                .addComponent(preferredField)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    @Override
    protected void doOKAction() {
        fillObject();
        super.doOKAction();
    }

    @Override
    protected @NotNull List<ValidationInfo> doValidateAll() {
        return super.doValidateAll();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (StringUtils.isBlank(nameField.getText())) {
            return new ValidationInfo(OpencellBundle.message("settings.environment.name.required"), nameField);
        }
        if (StringUtils.isBlank(urlField.getText()) || !isValidURL(urlField.getText())) {
            return new ValidationInfo(OpencellBundle.message("settings.environment.url.required"), urlField);
        }
        if (StringUtils.isBlank(usernameField.getText())) {
            return new ValidationInfo(OpencellBundle.message("settings.environment.username.required"), usernameField);
        }
        if (passwordField.getPassword().length == 0 && StringUtils.isBlank(passwordField.getEmptyText().getText())) {
            return new ValidationInfo(OpencellBundle.message("settings.environment.password.required"), passwordField);
        }

        return null;
    }

    private void fillForm(Environment environment) {
        nameField.setText(environment.getName());
        urlField.setText(environment.getUrl());
        usernameField.setText(environment.getUsername());
        passwordField.setPasswordIsStored(environment.hasPassword());
        preferredField.setSelected(environment.isPreferred());
    }

    private void fillObject() {
        if (this.environment == null) {
            this.environment = new Environment();
        }

        environment.updateName(nameField.getText());
        environment.setUrl(urlField.getText());
        environment.updateCredentials(usernameField.getText(), passwordField.getPassword());
        environment.setPreferred(preferredField.isSelected());
    }

    private boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
