package helpers

class NpmPackagesHelper {
  static Closure installation(String installationName) {
    return { project ->
      project / buildWrappers / 'jenkins.plugins.nodejs.tools.NpmPackagesBuildWrapper' << {
        nodeJSInstallationName(installationName);
      }
    }
  }
}