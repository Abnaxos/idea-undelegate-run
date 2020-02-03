package ch.raffael.idea.plugins.undelegaterun;

import javax.annotation.Nullable;

import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.task.ExecuteRunConfigurationTask;
import org.jetbrains.plugins.gradle.execution.build.GradleExecutionEnvironmentProvider;


public class UndelegateExecutionEnvironmentProvider implements GradleExecutionEnvironmentProvider {

    @Override
    public boolean isApplicable(ExecuteRunConfigurationTask task) {
        if (!(task.getRunProfile() instanceof ApplicationConfiguration)) {
            return false;
        }
        var runProfile = (ApplicationConfiguration)task.getRunProfile();
        return ProjectPlugin.isEnabledFor(runProfile, runProfile.getConfigurationModule().getModule());
    }

    @Override
    @Nullable
    public ExecutionEnvironment createExecutionEnvironment(Project project, ExecuteRunConfigurationTask task, Executor executor) {
        return null;
    }
}
