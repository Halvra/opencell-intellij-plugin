package com.github.halvra.opencell.settings.dialog;

import com.github.halvra.opencell.OpencellBundle;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ScriptInterfaceDialogWrapper extends DialogWrapper {
    private final JBTextField scriptInterfaceField = new JBTextField();

    @Getter
    private String value;

    public ScriptInterfaceDialogWrapper() {
        super(true); // use current window as parent

        init();
        setTitle(OpencellBundle.message("settings.scriptInterfaces.dialog"));
    }

    public ScriptInterfaceDialogWrapper(String value) {
        this();

        this.value = value;
        this.scriptInterfaceField.setText(value);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(OpencellBundle.message("settings.environment.name"), scriptInterfaceField)
                .getPanel();
    }

    @Override
    protected void doOKAction() {
        this.value = scriptInterfaceField.getText();
        super.doOKAction();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (StringUtils.isBlank(scriptInterfaceField.getText())) {
            return new ValidationInfo(OpencellBundle.message("settings.scriptInterfaces.required"), scriptInterfaceField);
        }

        return null;
    }
}
