package helpers

class MSBuildHelper {
  static Closure build(String buildFile, String args, Boolean warningsAsErrors = false) {
    return { project ->
      project / builders << {
        'hudson.plugins.msbuild.MsBuildBuilder' {
          msBuildName('MSBuild 4.0');
          msBuildFile(buildFile);
          cmdLineArgs(args);
          buildVariablesAsProperties(false);
          continueOnBuildFailure(false);
          unstableIfWarnings(warningsAsErrors);
        }
      }
    }
  }
}