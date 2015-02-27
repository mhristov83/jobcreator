package helpers

class VersionNumberHelper {
  static Closure versionNumber(String variableName, String versionNumberFormat) {
    return { project ->
      project / buildWrappers / 'org.jvnet.hudson.tools.versionnumber.VersionNumberBuilder' << {
        versionNumberString(versionNumberFormat);
        projectStartDate('1970-01-01 00:00:00.0 UTC');
        environmentVariableName(variableName);
        oBuildsToday(-1);
        oBuildsThisMonth(-1);
        oBuildsThisYear(-1);
        oBuildsAllTime(-1);
        skipFailedBuilds(false);
        useAsBuildDisplayName(false);
      }
    }
  }
}