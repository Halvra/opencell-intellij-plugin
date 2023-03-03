package com.github.halvra.opencell.settings;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.settings.dialog.EnvironmentDialogWrapper;
import com.github.halvra.opencell.settings.dialog.ScriptInterfaceDialogWrapper;
import com.github.halvra.opencell.settings.model.Environment;
import com.github.halvra.opencell.utils.ScriptUtil;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.project.Project;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.ListTableModel;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

@Getter
public class ProjectSettingsComponent {
    private final Project project;
    private final JPanel mainPanel;
    private final TableView<Environment> environmentTable;
    private final JBList<String> scriptInterfacesList;
    private final JButton scriptInterfacesAutoDetectButton;

    public ProjectSettingsComponent(Project project) {
        this.project = project;
        this.environmentTable = initEnvironmentTable();
        this.scriptInterfacesList = new JBList<>(new CollectionListModel<>());
        this.scriptInterfacesAutoDetectButton = new JButton(OpencellBundle.message("settings.scriptInterfaces.autoDetect"));
        this.mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(OpencellBundle.message("settings.environment"), ToolbarDecorator.createDecorator(environmentTable).setToolbarPosition(ActionToolbarPosition.LEFT).setAddAction(action -> {
                    EnvironmentDialogWrapper dialog = new EnvironmentDialogWrapper(environmentTable.getListTableModel().getRowCount() < 1);
                    if (dialog.showAndGet()) {
                        environmentTable.getListTableModel().addRow(dialog.getEnvironment());
                        updatePreferredEnvironment(dialog.getEnvironment());
                        environmentTable.updateUI();
                    }
                }).setRemoveAction(action -> {
                    environmentTable.getListTableModel().removeRow(environmentTable.getSelectedRow());
                    updatePreferredEnvironment(null);
                    environmentTable.updateUI();
                }).setEditAction(action -> {
                    EnvironmentDialogWrapper dialog = new EnvironmentDialogWrapper(environmentTable.getSelectedObject(), environmentTable.getListTableModel().getRowCount() == 1);
                    if (dialog.showAndGet()) {
                        updatePreferredEnvironment(dialog.getEnvironment());
                        environmentTable.updateUI();
                    }
                }).createPanel())
                .addLabeledComponent(OpencellBundle.message("settings.scriptInterfaces"), ToolbarDecorator.createDecorator(scriptInterfacesList).setAddAction(action -> {
                    ScriptInterfaceDialogWrapper dialog = new ScriptInterfaceDialogWrapper();
                    if (dialog.showAndGet()) {
                        ((CollectionListModel<String>) scriptInterfacesList.getModel()).add(dialog.getValue());
                        environmentTable.updateUI();
                    }
                }).setEditAction(action -> {
                    if (ScriptUtil.DEFAULT_SCRIPT_INTERFACE.equalsIgnoreCase(scriptInterfacesList.getSelectedValue())) {
                        return;
                    }

                    ScriptInterfaceDialogWrapper dialog = new ScriptInterfaceDialogWrapper(scriptInterfacesList.getSelectedValue());
                    if (dialog.showAndGet()) {
                        ((CollectionListModel<String>) scriptInterfacesList.getModel()).setElementAt(dialog.getValue(), scriptInterfacesList.getSelectedIndex());
                        environmentTable.updateUI();
                    }
                }).setRemoveAction(action -> {
                    if (ScriptUtil.DEFAULT_SCRIPT_INTERFACE.equalsIgnoreCase(scriptInterfacesList.getSelectedValue())) {
                        return;
                    }

                    ((CollectionListModel<String>) scriptInterfacesList.getModel()).remove(scriptInterfacesList.getSelectedIndex());
                }).disableUpDownActions().createPanel())
                .addComponent(this.scriptInterfacesAutoDetectButton)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        this.scriptInterfacesAutoDetectButton.addActionListener(actionEvent -> {
            updateScriptInterfaces();
        });
    }

    private TableView<Environment> initEnvironmentTable() {
        ListTableModel<Environment> model = new ListTableModel<>(buildEnvironmentColumnInfo());

        return new TableView<>(model);
    }

    private void updateScriptInterfaces() {
        var scriptInterfacesModel = ((CollectionListModel<String>) scriptInterfacesList.getModel());
        var detectedScriptInterfaces = ScriptUtil.getScriptsInterfaces(project);
        detectedScriptInterfaces.forEach(scriptInterface -> {
            if (!scriptInterfacesModel.contains(scriptInterface)) {
                scriptInterfacesModel.add(scriptInterface);
            }
        });
    }

    private void updatePreferredEnvironment(Environment environment) {
        if (this.environmentTable.getListTableModel().getItems().stream().noneMatch(Environment::isPreferred)) {
            this.environmentTable.getListTableModel().getItems().stream().findFirst().ifPresent(env -> env.setPreferred(true));
        } else if (environment.isPreferred()) {
            this.environmentTable.getListTableModel().getItems().forEach(env -> env.setPreferred(env.equals(environment)));
        }
    }

    private ColumnInfo[] buildEnvironmentColumnInfo() {
        return new ColumnInfo[]{new ColumnInfo<Environment, String>(OpencellBundle.message("settings.environment.name")) {
            @Nullable
            @Override
            public String valueOf(Environment e) {
                return e.getName();
            }

            @Override
            public void setValue(Environment environment, String value) {
                environment.setName(value);
            }
        }, new ColumnInfo<Environment, String>(OpencellBundle.message("settings.environment.url")) {
            @Nullable
            @Override
            public String valueOf(Environment e) {
                return e.getUrl();
            }

            @Override
            public void setValue(Environment environment, String value) {
                environment.setUrl(value);
            }
        }, new ColumnInfo<Environment, Boolean>(OpencellBundle.message("settings.environment.preferred")) {
            @Override
            public Boolean valueOf(Environment e) {
                return e.isPreferred();
            }

            @Override
            public TableCellRenderer getRenderer(Environment environment) {
                return new BooleanTableCellRenderer();
            }

            @Override
            public void setValue(Environment environment, Boolean value) {
                environment.setPreferred(value);
            }
        }
        };
    }
}
