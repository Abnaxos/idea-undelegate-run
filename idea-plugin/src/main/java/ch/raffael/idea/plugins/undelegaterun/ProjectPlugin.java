package ch.raffael.idea.plugins.undelegaterun;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("DefaultAnnotationParam")
@State(name = "undelegate-run", reloadable = true, storages = @Storage("undelegate-run.xml"))
public class ProjectPlugin implements PersistentStateComponent<ProjectPlugin.State> {

    private static final Logger LOG = Logger.getInstance(ProjectPlugin.class);

    private final Object stateLock = new Object();
    private State state;

    public ProjectPlugin() {
        synchronized (stateLock) {
            state = new State();
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public static boolean isEnabledFor(RunProfile runProfile, @Nullable Module module) {
        if (module == null) {
            return false;
        }
        var state = module.getProject().getComponent(ProjectPlugin.class).getState();
        if (!state.isEnabled()) {
            return false;
        }
        if (isExcluded(state.getExcludedModules(), module.getName())) {
            return false;
        }
        if (isExcluded(state.getExcludedRunConfigurations(), runProfile.getName())) {
            return false;
        }
        return true;
    }

    public static boolean isExcluded(String pattern, String string) {
        if (pattern.isBlank()) {
            return false;
        }
        try {
            return Pattern.compile(pattern).matcher(string).find();
        } catch (PatternSyntaxException e) {
            LOG.error("Illegal pattern: " + pattern, e);
            return false;
        }
    }

    @Override
    @NotNull
    public State getState() {
        synchronized (stateLock) {
            return new State(state);
        }
    }

    @Override
    public void loadState(@NotNull State state) {
        synchronized (stateLock) {
            this.state = new State(state);
        }
    }

    public static class State {

        private boolean enabled = true;
        private String excludedModules = "";
        private String excludedRunConfigurations = "";

        public State() {
        }

        public State(State that) {
            this.enabled = that.enabled;
            this.excludedModules = that.excludedModules;
            this.excludedRunConfigurations = that.excludedRunConfigurations;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getExcludedModules() {
            return excludedModules;
        }

        public void setExcludedModules(String excludedModules) {
            this.excludedModules = excludedModules == null ? "" : excludedModules;
        }

        public String getExcludedRunConfigurations() {
            return excludedRunConfigurations;
        }

        public void setExcludedRunConfigurations(String excludedRunConfigurations) {
            this.excludedRunConfigurations = excludedRunConfigurations == null ? "" : excludedRunConfigurations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            //noinspection ObjectEquality
            if (o == null || getClass() != o.getClass()) return false;
            State that = (State)o;
            if (enabled != that.enabled) return false;
            if (!excludedModules.equals(that.excludedModules)) return false;
            return excludedRunConfigurations.equals(that.excludedRunConfigurations);
        }

        @Override
        public int hashCode() {
            int result = (enabled ? 1 : 0);
            result = 31 * result + excludedModules.hashCode();
            result = 31 * result + excludedRunConfigurations.hashCode();
            return result;
        }
    }

}
