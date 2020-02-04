package local

import org.gradle.api.GradleException
import org.gradle.api.Project

import java.util.regex.Pattern

class GitInfo {

    static final P_EXECUTABLE = 'git.executable'
    static final String VERSION_RE = /(?<v>\d+(?:\.\d+)*)/
    static final NEWLINE_RE = ~/\r?\n|\r/

    final Project project
    final String gitExecutable

    Pattern releaseBranchRE = ~('(?:release|hotfix)/' + VERSION_RE + '')
    Pattern releaseTagRE = ~VERSION_RE

    String branch
    String hash
    String version = 'DEVELOP-SNAPSHOT'
    boolean snapshot = true

    GitInfo(Project project) {
        this.project = project
        gitExecutable = project.properties[P_EXECUTABLE] ?: project.gradle.properties[P_EXECUTABLE] ?: 'git'
    }

    static load(Project project) {
        return new GitInfo(project).load()
    }

    def load() {
        branch = gitOneLine 'rev-parse', '--abbrev-ref', 'HEAD'
        hash = gitOneLine 'rev-parse', 'HEAD'
        def releases = git('tag', '-l', '--points-at', 'HEAD')
                .collect {releaseTagRE.matcher(it)}
                .findAll {it.matches()}
                .collect {it.group('v')}
        if (!releases) {
            def rcMatcher = releaseBranchRE.matcher(this.branch)
            if (rcMatcher.matches()) {
                version = rcMatcher.group('v') + '-SNAPSHOT'
                snapshot = true
            }
        } else if (releases.size() == 1) {
            version = releases[0]
            snapshot = false
        } else {
            throw new GradleException("No distinct version tag found: $releases")
        }
        project.logger.quiet "Detected version: $version (snapshot: $snapshot)"
        return this
    }

    String gitOneLine(Object... gitArgs) {
        def lines = git(gitArgs)
        if (lines.size() != 1) {
            throw new GradleException("Expected one line, got $lines")
        }
        return lines[0]
    }

    List<String> git(Object... gitArgs) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        project.exec {
            workingDir = project.rootDir
            standardOutput = out
            commandLine = [gitExecutable]+gitArgs.collect { it as String }
        }
        NEWLINE_RE.split(new String(out.toByteArray())).collect {it.trim()}.findAll {!it.isEmpty()} as List
    }
}
