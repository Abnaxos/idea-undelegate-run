package ch.raffael.idea.plugins.undelegaterun;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Settings implements Configurable {

    private static final Logger LOG = Logger.getInstance(Settings.class);

    private final ProjectPlugin plugin;

    private ProjectPlugin.State state;

    public Settings(Project project) {
        this.plugin = project.getComponent(ProjectPlugin.class);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Un-Delegate Run Actions";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        // nullability for searchable index builder
        state = plugin().getState();
        var container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        var settings = new JPanel(new VerticalFlowLayout(true, false));
        container.add(settings, BorderLayout.NORTH);
        // enabled check box
        var line = new JPanel(new BorderLayout());
        var enabled = new JCheckBox("Un-Delegate Run Actions");
        enabled.setSelected(state.isEnabled());
        enabled.addChangeListener(e -> state.setEnabled(enabled.isSelected()));
        line.add(enabled, BorderLayout.WEST);
        settings.add(line);
        // exclude modules regex
        line = new JPanel(new BorderLayout(5, 5));
        line.add(new JLabel("Excluded Modules (Regex contains)"), BorderLayout.WEST);
        var excludedModules = new JTextField();
        excludedModules.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        excludedModules.setText(state.getExcludedModules());
        excludedModules.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                state.setExcludedModules(excludedModules.getText());
            }
        });
        line.add(excludedModules, BorderLayout.CENTER);
        settings.add(line);
        // exclude run configurations regex
        line = new JPanel(new BorderLayout(5, 5));
        line.add(new JLabel("Excluded Run Configurations (Regex contains)"), BorderLayout.WEST);
        var excludedRunConfs = new JTextField();
        excludedRunConfs.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        excludedRunConfs.setText(state.getExcludedRunConfigurations());
        excludedRunConfs.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                state.setExcludedRunConfigurations(excludedRunConfs.getText());
            }
        });
        line.add(excludedRunConfs, BorderLayout.CENTER);
        settings.add(line);
        return container;
    }

    @Override
    public boolean isModified() {
        return !plugin().getState().equals(state);
    }

    @Override
    public void apply() {
        plugin().loadState(state);
    }

    private ProjectPlugin plugin() {
        if (plugin == null) {
            LOG.error("Plugin is null; that should only happen during indexing");
            return new ProjectPlugin();
        } else {
            return plugin;
        }
    }
}
