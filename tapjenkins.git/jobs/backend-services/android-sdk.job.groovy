import helpers.*

def defaultBranch = '*/master'
def projectRepo = 'https://github.com/telerik/everlive-android-sdk.git'

def job = JobFactory.createLinux(this,'Android-SDK')

GitHelper.useGitHubRepo(job, projectRepo, defaultBranch)

job.with {
  triggers{
    cron '@daily'
  }

  steps {
    ant(null, 'androidsdkWithTests.xml')
  }

  publishers {
    archiveJunit 'out/test/results/*.xml'

    archiveArtifacts {
      pattern 'out/artifacts/EverliveSDK.Android.zip'
      latestOnly true
    }
  }
}