import helpers.*

def defaultBranch = 'origin/master'
def defaultEnv = 'Test'
def projectRepo = 'https://github.com/telerik/eldemo-docs.git'

def job = JobFactory.createLinux(this,'BS-Documentation', 'master')

GitHelper.useGitHubRepo(job, projectRepo, '\${Branch}')

job.with {
  parameters {
    stringParam('Branch', defaultBranch, 'The branch in the Git repository where the documentation is obtained from.')
    stringParam('Environment', defaultEnv, 'Environment where documentation is deployed. Could be Test or Live.')
  }

  configure VersionNumberHelper.versionNumber('BuildNumber', '${BUILDS_TODAY}')

  steps {
    shell 'bash _deployment/build-and-deploy.sh $BuildNumber _site AppDirector/Drops/BackendServices/BackendServices.Docs $Environment 600'
  }
}