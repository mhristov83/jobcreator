package helpers

class ArtifactoryHelper {
  static Closure deployArtifacts(String server, String serverName, String repo, String deployPatternArg) {
    return { project ->
      project / buildWrappers / 'org.jfrog.hudson.generic.ArtifactoryGenericConfigurator' << {
        details {
          artifactoryName(serverName)
          repositoryKey(repo)
          snapshotsRepositoryKey(repo)
          artifactoryUrl(server)
        }
        deployPattern(deployPatternArg)
        deployBuildInfo(true)
        includeEnvVars(false)
        discardOldBuilds(false)
        discardBuildArtifacts(true)
        multiConfProject(false)
      }
    }
  }
}