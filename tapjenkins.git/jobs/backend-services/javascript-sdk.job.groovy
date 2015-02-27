import helpers.*

def defaultBranch = 'origin/master'
def projectRepo = 'https://github.com/telerik/everlive-js-sdk.git'

def job = JobFactory.createLinux(this,'JavaScript-SDK')

GitHelper.useGitHubRepo(job, projectRepo, '${Branch}')

job.with {
  parameters {
    stringParam('Branch', defaultBranch, 'The branch in the Git repository where the source code is obtained from.')
  }

  triggers{
    cron '@daily'
  }

  steps {
    shell 'bash automation/RunCI.sh'
  }

  publishers {
    archiveJunit 'test/testResultsFixed.xml'

    archiveArtifacts {
      pattern 'out/EverliveSDK.JS.zip'
      latestOnly true
    }
  }
}
