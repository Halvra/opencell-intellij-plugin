package com.github.halvra.opencell.settings;

import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EnvironmentDialogWrapper extends DialogWrapper {
    private final JBTextField nameField = new JBTextField();
    private final JBTextField urlField = new JBTextField();
    private final JBTextField usernameField = new JBTextField();
    private final JBPasswordField passwordField = new JBPasswordField();
    private final JBCheckBox preferredField = new JBCheckBox("Preferred ?");

    private Environment environment;

    public EnvironmentDialogWrapper() {
        super(true); // use current window as parent
        init();
        setTitle("Add/Edit Environment");
    }

    public EnvironmentDialogWrapper(Environment environment) {
        this();

        this.environment = environment;
        fillForm(environment);
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Name", nameField)
                .addLabeledComponent("URL", urlField)
                .addLabeledComponent("Username", usernameField)
                .addLabeledComponent("Password", passwordField)
                .addComponent(preferredField)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    @Override
    protected void doOKAction() {
        fillObject();
        super.doOKAction();
    }

    private void fillForm(Environment environment) {
        nameField.setText(environment.getName());
        urlField.setText(environment.getUrl());
        usernameField.setText(environment.getUsername());
        passwordField.setText(environment.getPassword());
        preferredField.setSelected(environment.isPreferred());
    }

    private void fillObject() {
        if (this.environment == null) {
            this.environment = new Environment();
        }

        environment.setName(nameField.getText());
        environment.setUrl(urlField.getText());
        environment.setAuthorization(usernameField.getText(), String.copyValueOf(passwordField.getPassword()));
        environment.setPreferred(preferredField.isSelected());
    }
}
