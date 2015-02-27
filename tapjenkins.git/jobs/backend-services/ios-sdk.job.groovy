import helpers.*

def defaultBranch = '*/master'
def whereToRun = 'ios-slave'
def projectRepo = 'https://github.com/telerik/everlive-ios-sdk.git'

def job = JobFactory.createDefault(this,'iOS-SDK')

GitHelper.useGitHubRepo(job, projectRepo, defaultBranch)

job.with {
  label whereToRun

  triggers{
    cron '@daily'
  }

  steps {
    shell '/usr/local/Cellar/xctool/0.1.14/bin/xctool -project EverliveSDK.xcodeproj -scheme EverliveSDKTests test -sdk iphonesimulator -reporter junit:testResults.xml || true'
  }

  publishers {
    archiveJunit 'testResults.xml'
  }
}
