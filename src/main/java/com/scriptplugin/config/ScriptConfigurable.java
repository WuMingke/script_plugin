package com.scriptplugin.config;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Settings 中的脚本配置页面
 */
public class ScriptConfigurable implements Configurable {
    private JPanel myMainPanel;
    private JBTable scriptTable;
    private DefaultTableModel tableModel;
    private ToolbarDecorator toolbarDecorator;
    private JPanel tablePanel;
    
    private ScriptConfigService configService;
    private List<ScriptConfig> scripts;

    public ScriptConfigurable() {
        configService = ScriptConfigService.getInstance();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Script Runner";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        // 创建表格
        String[] columnNames = {"名称", "脚本路径", "工作目录", "参数", "描述"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scriptTable = new JBTable(tableModel);
        scriptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scriptTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scriptTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        scriptTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        scriptTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        scriptTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        scriptTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        // 创建工具栏
        toolbarDecorator = ToolbarDecorator.createDecorator(scriptTable)
                .setAddAction(anActionButton -> addScript())
                .setRemoveAction(anActionButton -> removeScript())
                .setEditAction(anActionButton -> editScript())
                .disableUpDownActions();

        tablePanel = toolbarDecorator.createPanel();

        // 创建主面板
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(new JBLabel("配置要运行的脚本："))
                .addComponent(tablePanel, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        loadScripts();
        return myMainPanel;
    }

    private void loadScripts() {
        tableModel.setRowCount(0);
        scripts = configService.getScripts();
        for (ScriptConfig script : scripts) {
            tableModel.addRow(new Object[]{
                    script.getName(),
                    script.getPath(),
                    script.getWorkingDirectory(),
                    script.getArguments(),
                    script.getDescription()
            });
        }
    }

    private void addScript() {
        ScriptDialog dialog = new ScriptDialog(null);
        if (dialog.showAndGet()) {
            ScriptConfig script = dialog.getScript();
            configService.addScript(script);
            loadScripts();
        }
    }

    private void removeScript() {
        int selectedRow = scriptTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < scripts.size()) {
            int result = Messages.showYesNoDialog(
                    "确定要删除脚本 \"" + scripts.get(selectedRow).getName() + "\" 吗？",
                    "确认删除",
                    Messages.getQuestionIcon()
            );
            if (result == Messages.YES) {
                configService.removeScript(scripts.get(selectedRow));
                loadScripts();
            }
        } else {
            Messages.showWarningDialog("请先选择一个脚本", "提示");
        }
    }

    private void editScript() {
        int selectedRow = scriptTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < scripts.size()) {
            ScriptDialog dialog = new ScriptDialog(scripts.get(selectedRow));
            if (dialog.showAndGet()) {
                ScriptConfig updatedScript = dialog.getScript();
                configService.updateScript(selectedRow, updatedScript);
                loadScripts();
            }
        } else {
            Messages.showWarningDialog("请先选择一个脚本", "提示");
        }
    }

    @Override
    public boolean isModified() {
        return false; // 我们直接保存，不需要检查修改
    }

    @Override
    public void apply() throws ConfigurationException {
        // 配置已通过服务直接保存
    }

    /**
     * 脚本编辑对话框
     */
    private static class ScriptDialog extends DialogWrapper {
        private JBTextField nameField;
        private JBTextField pathField;
        private JBTextField workingDirField;
        private JBTextArea commandsField;
        private JBTextField descriptionField;
        private JButton browsePathButton;
        private JButton browseWorkingDirButton;
        private ScriptConfig script;

        protected ScriptDialog(@Nullable ScriptConfig script) {
            super(true);
            this.script = script;
            setTitle(script == null ? "添加脚本" : "编辑脚本");
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            nameField = new JBTextField();
            pathField = new JBTextField();
            workingDirField = new JBTextField();
            // 使用多行文本区域支持输入多个命令
            commandsField = new JBTextArea(5, 50);
            commandsField.setLineWrap(true);
            commandsField.setWrapStyleWord(true);
            commandsField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            descriptionField = new JBTextField();

            // 脚本路径浏览按钮
            browsePathButton = new JButton("浏览...");
            browsePathButton.addActionListener(e -> {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
                descriptor.setTitle("选择脚本文件");
                descriptor.setDescription("请选择要运行的脚本文件");
                
                // 如果已有路径，默认打开该路径的父目录
                VirtualFile baseDir = null;
                if (!pathField.getText().trim().isEmpty()) {
                    File existingFile = new File(pathField.getText().trim());
                    if (existingFile.exists() && existingFile.getParentFile() != null) {
                        VirtualFile parentDir = com.intellij.openapi.vfs.LocalFileSystem.getInstance()
                                .findFileByPath(existingFile.getParentFile().getAbsolutePath());
                        if (parentDir != null) {
                            baseDir = parentDir;
                        }
                    }
                }
                
                VirtualFile[] selectedFiles = FileChooser.chooseFiles(descriptor, null, baseDir);
                if (selectedFiles.length > 0) {
                    pathField.setText(selectedFiles[0].getPath());
                }
            });

            // 工作目录浏览按钮
            browseWorkingDirButton = new JButton("浏览...");
            browseWorkingDirButton.addActionListener(e -> {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                descriptor.setTitle("选择工作目录");
                descriptor.setDescription("请选择脚本运行的工作目录");
                
                // 如果脚本路径已设置，默认打开脚本所在目录
                VirtualFile baseDir = null;
                if (!pathField.getText().trim().isEmpty()) {
                    File scriptFile = new File(pathField.getText().trim());
                    if (scriptFile.exists() && scriptFile.getParentFile() != null) {
                        baseDir = com.intellij.openapi.vfs.LocalFileSystem.getInstance()
                                .findFileByPath(scriptFile.getParentFile().getAbsolutePath());
                    }
                }
                // 如果工作目录已设置，使用工作目录作为默认路径
                if (baseDir == null && !workingDirField.getText().trim().isEmpty()) {
                    File workingDir = new File(workingDirField.getText().trim());
                    if (workingDir.exists()) {
                        baseDir = com.intellij.openapi.vfs.LocalFileSystem.getInstance()
                                .findFileByPath(workingDir.getAbsolutePath());
                    }
                }
                
                VirtualFile[] selectedDirs = FileChooser.chooseFiles(descriptor, null, baseDir);
                if (selectedDirs.length > 0) {
                    workingDirField.setText(selectedDirs[0].getPath());
                }
            });

            JPanel pathPanel = new JPanel(new BorderLayout());
            pathPanel.add(pathField, BorderLayout.CENTER);
            pathPanel.add(browsePathButton, BorderLayout.EAST);

            JPanel workingDirPanel = new JPanel(new BorderLayout());
            workingDirPanel.add(workingDirField, BorderLayout.CENTER);
            workingDirPanel.add(browseWorkingDirButton, BorderLayout.EAST);

            if (script != null) {
                nameField.setText(script.getName());
                pathField.setText(script.getPath());
                workingDirField.setText(script.getWorkingDirectory());
                commandsField.setText(script.getArguments());
                descriptionField.setText(script.getDescription());
            }

            // 为命令字段添加滚动条
            JBScrollPane commandsScrollPane = new JBScrollPane(commandsField);
            commandsScrollPane.setPreferredSize(new Dimension(600, 120));
            
            JPanel panel = FormBuilder.createFormBuilder()
                    .addLabeledComponent("名称:", nameField, 1, false)
                    .addLabeledComponent("脚本路径:", pathPanel, 1, false)
                    .addLabeledComponent("工作目录:", workingDirPanel, 1, false)
                    .addLabeledComponent("命令/参数:", commandsScrollPane, 1, false)
                    .addLabeledComponent("描述:", descriptionField, 1, false)
                    .addComponentFillVertically(new JPanel(), 0)
                    .getPanel();
            
            // 设置对话框默认宽度为 600 像素
            panel.setPreferredSize(new Dimension(600, panel.getPreferredSize().height));
            
            return panel;
        }

        @Override
        protected void doOKAction() {
            if (nameField.getText().trim().isEmpty()) {
                Messages.showErrorDialog("请输入脚本名称", "错误");
                return;
            }
            // 脚本路径可以为空，如果为空则使用命令字段的内容作为脚本
            String scriptPath = pathField.getText().trim();
            String commands = commandsField.getText().trim();
            
            if (scriptPath.isEmpty() && commands.isEmpty()) {
                Messages.showErrorDialog("请选择脚本路径或输入命令", "错误");
                return;
            }
            
            script = new ScriptConfig(
                    nameField.getText().trim(),
                    scriptPath,
                    workingDirField.getText().trim(),
                    commands,
                    descriptionField.getText().trim()
            );
            super.doOKAction();
        }

        public ScriptConfig getScript() {
            return script;
        }
    }
}

