import helpers.*

def defaultBranch = 'origin/develop'

def job = JobFactory.createWindows(this,'DotNet-SDK')

GitHelper.useGitLabBsRepo(job, '${Branch}', 'SDK/DotNet/.*', '', GitHelper.windowsRepoReference)

job.with {
  parameters {
    stringParam('Branch', defaultBranch, 'The branch in the Git repository where the source code is obtained from.')
  }

  triggers{
    cron '@daily'
    scm ''
  }

  configure VersionNumberHelper.versionNumber('BUILD_VERSION', '${JOB_NAME}_${BUILD_YEAR, XXXX}.${BUILD_MONTH, XX}.${BUILD_DAY, XX}.${BUILDS_TODAY}')

  wrappers {
    preBuildCleanup {
      includePattern 'SDK/DotNet/Build/*'
    }
  }

  def msbuildArgs = '/verbosity:minimal /p:Configuration="Release";Platform="Any CPU";GenerateProjectSpecificOutputFolder=false;BuildName=$BUILD_VERSION;LastChangeSet=$GIT_COMMIT'
  def trxFileName = 'SDK\\DotNet\\Build\\Everlive.SDK.Tests\\TestResults.trx'
  def mstestArgs = '/testsettings:"SDK\\DotNet\\Everlive SDK Tests\\Default.testsettings"'

  configure MSBuildHelper.build('SDK\\DotNet\\Everlive SDK.sln', msbuildArgs)

  configure MSTestHelper.run('SDK\\DotNet\\Build\\Everlive.SDK.Tests\\Everlive.SDK.Tests.dll', trxFileName, mstestArgs)

  configure MSTestHelper.importResult(trxFileName)

  publishers {
    archiveArtifacts {
      pattern 'SDK\\DotNet\\Build\\Packages\\*.zip'
      latestOnly true
    }
  }
}