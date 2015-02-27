import helpers.*

def defaultBranch = 'origin/develop'

def job = JobFactory.createWindows(this,'DataLink-Server')

GitHelper.useGitLabBsRepo(job, '${Branch}', 'DataLink/.*', '', GitHelper.windowsRepoReference)

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
      includePattern 'DataLink/Build/*'
      deleteDirectories true
    }
  }

  def msbuildArgs = '/p:Configuration="Release";Platform="Any CPU";DeployOnBuild=true;PublishProfile=CreatePackage;BuildName=$BUILD_VERSION;LastChangeSet=$GIT_COMMIT'
  def testContainers = """DataLink\\Build\\TestsOutput\\Telerik.Everlive.DataLink.Infrastructure.Tests.dll
  DataLink\\Build\\TestsOutput\\Telerik.Everlive.DataLink.Tests.dll
  DataLink\\Build\\TestsOutput\\Telerik.Everlive.DataLink.WebApi.Tests.dll"""
  def mstestArgs = '/testsettings:"SDK\\DotNet\\Everlive SDK Tests\\Default.testsettings"'

  configure MSBuildHelper.build('DataLink\\Automation\\RestorePackages.proj', '')
  configure MSBuildHelper.build('DataLink\\Product\\Telerik.Everlive.DataLink.sln', msbuildArgs, true)

  configure MSTestHelper.run(testContainers, 'DataLink\\Build\\MsSqlResults.trx', '/testsettings:DataLink\\Testing\\Settings\\MsSql.testsettings')
  configure MSTestHelper.run(testContainers, 'DataLink\\Build\\OracleResults.trx', '/testsettings:DataLink\\Testing\\Settings\\Oracle-Ora10.testsettings')
  configure MSTestHelper.run(testContainers, 'DataLink\\Build\\MySqlResults.trx', '/testsettings:DataLink\\Testing\\Settings\\MySql.testsettings')
  configure MSTestHelper.run(testContainers, 'DataLink\\Build\\PostgreSqlResults.trx', '/testsettings:DataLink\\Testing\\Settings\\PostgreSql.testsettings')

  configure MSTestHelper.importResult('DataLink\\Build\\*.trx')

  publishers {
    archiveArtifacts {
      pattern 'DataLink\\Build\\WebDeploy\\TelerikDataLinkServer.zip'
      latestOnly true
    }
  }
}