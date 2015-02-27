import helpers.*

def param_branch = 'BS_Branch'
def defaultBranch = 'origin/develop'
def param_branch_description = 'The branch in the Git repository where the source code is obtained from.'

def artifactoryServerUrl = 'https://artifactsrvbg01.telerik.com/artifactory'
def artifactoryServerName = '1441311328@1422449614609'

def environments = ['dev', 'dev3', 'sit']

for (env in environments) {
  def deployEnv = env
  def envDisplayName = env.toUpperCase()
  def deployPackagesJobName = 'Pipeline-' + envDisplayName + '-Deploy'

  if (env == 'sit') {
    envDisplayName = 'Release'
  }

  def initialJobName = 'Pipeline-' + envDisplayName + '-Init'
  def getVersionJobName = 'Pipeline-' + envDisplayName + '-Version'
  def createPackagesJobName = 'Pipeline-' + envDisplayName + '-Create-Packages'

  // Step 1
  def initJob = JobFactory.createDefault(this, initialJobName)

  initJob.with {
    parameters {
      stringParam('BS_Name', '', 'Description of the package to deploy and test. Example: Image Server fixes to DEV env.')
      stringParam( param_branch, defaultBranch, param_branch_description)
    }

    wrappers {
      buildName('${ENV,var="BS_Name"}')
    }

    deliveryPipelineConfiguration('Initialize', 'Initialize ' + envDisplayName)

    configure DownstreamHelper.parameterized(getVersionJobName)
    configure DownstreamHelper.useCurrentBuildParams()
  }

  // Step 2
  def getVersionJob = JobFactory.createDefault(this, getVersionJobName)

  GitHelper.useGitLabBsRepo(getVersionJob, '${' + param_branch + '}', '', '', GitHelper.windowsRepoReference)

  getVersionJob.with {
    label 'windows' // The workspace should not be deleted at the end, so the props file exists

    parameters {
      stringParam( param_branch, defaultBranch, param_branch_description)
    }

    deliveryPipelineConfiguration('Build', 'Get Version')

    steps {
      batchFile 'powershell Automation\\GetVersionFromPackageJson.ps1'
    }

    configure DownstreamHelper.parameterized(createPackagesJobName)
    configure DownstreamHelper.useCurrentBuildParams()
    configure DownstreamHelper.useParamsFromFile()
  }

  // Step 3
  def createPackagesJob = JobFactory.createWindows(this, createPackagesJobName)

  GitHelper.useGitLabBsRepo(createPackagesJob, '${' + param_branch + '}', '', '', GitHelper.windowsRepoReference)

  createPackagesJob.with {
    parameters {
      stringParam( param_branch, defaultBranch, param_branch_description)
    }

    deliveryPipelineConfiguration('Build', 'Create Packages')

    wrappers {
      deliveryPipelineVersion('${ENV,var="BS_VERSION"}-${GIT_REVISION,length=7}', true)
      buildName('${PIPELINE_VERSION}')
    }

    configure ArtifactoryHelper.deployArtifacts(artifactoryServerUrl, artifactoryServerName, 'tap-backendservices',
"""Packages/APIServer*.zip=>snapshots/APIServer
Packages/CodeServer*.zip=>snapshots/CodeServer
Packages/ContainerManagerServer*.zip=>snapshots/ContainerManagerServer
Packages/DebugServer*.zip=>snapshots/DebugServer
Packages/NotificationsSender*.zip=>snapshots/NotificationsSender
Packages/NotificationsServer*.zip=>snapshots/NotificationsServer
Packages/Scheduler*.zip=>snapshots/Scheduler
ImageServer/build/ImageServer*.zip=>snapshots/ImageServer
connection-router*.zip=>snapshots/connection-router
Portal/package/PortalServer*.zip=>snapshots/PortalServer""")

    def msbuildArgs = '/property:Configuration="Release";PackageLocation=${WORKSPACE}\\Temp\\APIServer.zip;_PackageTempDir=${WORKSPACE}\\Output\\APIServer\\;OutDir=${WORKSPACE};BS_VERSION=${PIPELINE_VERSION} /target:Package'
    configure MSBuildHelper.build('Server\\APIServer\\APIServer.csproj', msbuildArgs)

    msbuildArgs = '/property:Configuration="Release";PackageLocation=${WORKSPACE}\\Temp\\CodeServer.zip;_PackageTempDir=${WORKSPACE}\\Output\\CodeServer\\;OutDir=${WORKSPACE};BS_VERSION=${PIPELINE_VERSION} /target:Package'
    configure MSBuildHelper.build('Server\\APIServer\\CodeServer.csproj', msbuildArgs)

    msbuildArgs = '/property:Configuration="Release";PackageLocation=${WORKSPACE}\\Temp\\ContainerManagerServer.zip;_PackageTempDir=${WORKSPACE}\\Output\\ContainerManagerServer\\;OutDir=${WORKSPACE};BS_VERSION=${PIPELINE_VERSION} /target:Package'
    configure MSBuildHelper.build('Server\\APIServer\\ContainerManagerServer.csproj', msbuildArgs)

    msbuildArgs = '/property:Configuration="Release";PackageLocation=${WORKSPACE}\\Temp\\DebugServer.zip;_PackageTempDir=${WORKSPACE}\\Output\\DebugServer\\;OutDir=${WORKSPACE};BS_VERSION=${PIPELINE_VERSION} /target:Package'
    configure MSBuildHelper.build('Server\\APIServer\\DebugServer.csproj', msbuildArgs)

    msbuildArgs = '/property:Configuration="Release";PackageLocation=${WORKSPACE}\\Temp\\NotificationsSender.zip;_PackageTempDir=${WORKSPACE}\\Output\\NotificationsSender\\;OutDir=${WORKSPACE};BS_VERSION=${PIPELINE_VERSION} /target:Package'
    configure MSBuildHelper.build('Server\\APIServer\\NotificationsSender.csproj', msbuildArgs)

    msbuildArgs = '/property:Configuration="Release";PackageLocation=${WORKSPACE}\\Temp\\NotificationsServer.zip;_PackageTempDir=${WORKSPACE}\\Output\\NotificationsServer\\;OutDir=${WORKSPACE};BS_VERSION=${PIPELINE_VERSION} /target:Package'
    configure MSBuildHelper.build('Server\\APIServer\\NotificationsServer.csproj', msbuildArgs)

    msbuildArgs = '/property:Configuration="Release";PackageLocation=${WORKSPACE}\\Temp\\Scheduler.zip;_PackageTempDir=${WORKSPACE}\\Output\\Scheduler\\;OutDir=${WORKSPACE};BS_VERSION=${PIPELINE_VERSION} /target:Package'
    configure MSBuildHelper.build('Server\\APIServer\\Scheduler.csproj', msbuildArgs)

    steps {
      ant(null, 'ImageServer\\build.xml') {
        prop('BS_VERSION', '${PIPELINE_VERSION}')
      }

      batchFile 'powershell Automation\\CreateConnectionRouterPackage.ps1 -outDir %WORKSPACE% -version %PIPELINE_VERSION%'

      batchFile 'powershell Automation\\CreatePortalServerPackage.ps1 -outDir %WORKSPACE% -version %PIPELINE_VERSION%'
    }

    configure DownstreamHelper.parameterized(deployPackagesJobName)
    configure DownstreamHelper.useCurrentBuildParams()
  }

  // Step 4
  JobFactory.createDeploy(this, deployPackagesJobName, 'ubuntu-ansible', deployEnv)

  // Step 5 - Run Tests (See integration-tests.job.groovy)

  // Step 6 - Deploy on UAT
  if ( env == 'sit') {
    deployEnv = 'uat'

    JobFactory.createDeploy(this, 'Pipeline-UAT-Deploy', 'aws-bastion-uat', deployEnv)
  }

  // Step 7 - Run Tests on UAT (See integration-tests.job.groovy)

  // Step 8 - Deploy on LIVE
  if ( env == 'sit') {
    deployEnv = 'live'

    JobFactory.createDeploy(this, 'Pipeline-LIVE-Deploy', 'aws-bastion-live', deployEnv)
  }

  // Step 9 - Run Tests on LIVE (See integration-tests.job.groovy)

  // Delivery Pipeline View
  view(type: DeliveryPipelineView ) {
    name('BS ' + envDisplayName + ' Pipeline')
    pipelineInstances(3)
    columns(1)
    sorting(Sorting.LAST_ACTIVITY) // Possible value are Sorting.NONE, Sorting.TITLE and Sorting.LAST_ACTIVITY.
    enableManualTriggers(true)
    showChangeLog(true)

    pipelines {
      component('Backend Services', initialJobName)
    }
  }
}