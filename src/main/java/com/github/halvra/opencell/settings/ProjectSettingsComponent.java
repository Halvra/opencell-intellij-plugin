package com.github.halvra.opencell.settings;

import com.github.halvra.opencell.OpencellBundle;
import com.github.halvra.opencell.settings.model.Environment;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class ProjectSettingsComponent {

    private final JPanel mainPanel;
    private final TableView<Environment> environmentTable;

    public JPanel getPanel() {
        return mainPanel;
    }

    public ProjectSettingsComponent() {
        this.environmentTable = initEnvironmentTable();
        this.mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(OpencellBundle.message("settings.environment"), ToolbarDecorator.createDecorator(environmentTable).setToolbarPosition(ActionToolbarPosition.LEFT).setAddAction(action -> {
                    EnvironmentDialogWrapper dialog = new EnvironmentDialogWrapper();
                    if (dialog.showAndGet()) {
                        environmentTable.getListTableModel().addRow(dialog.getEnvironment());
                        environmentTable.updateUI();
                    }
                }).setRemoveAction(action -> {
                    environmentTable.getListTableModel().removeRow(environmentTable.getSelectedRow());
                }).setEditAction(action -> {
                    EnvironmentDialogWrapper dialog = new EnvironmentDialogWrapper(environmentTable.getSelectedObject());
                    if (dialog.showAndGet()) {
                        environmentTable.updateUI();
                    }
                }).createPanel())
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public TableView<Environment> getEnvironmentTable() {
        return environmentTable;
    }

    private TableView<Environment> initEnvironmentTable() {
        ListTableModel<Environment> model = new ListTableModel<>(buildEnvironmentColumnInfo());

        return new TableView<>(model);
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
