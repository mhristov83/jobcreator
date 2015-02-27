import helpers.*

def defaultBranch = ''

def job = JobFactory.createWindows(this,'Image-Server')

GitHelper.useGitLabBsRepo(job, '${Branch}', '', '', GitHelper.windowsRepoReference)

job.with {
  description 'CI build for the image server'

  parameters {
    stringParam('Branch', defaultBranch, 'The branch in the Git repository where the source code is obtained from.')
  }

  steps {
    ant(null, 'ImageServer/build.xml')
  }

  publishers {
    archiveJunit 'ImageServer/build/test/results/*.xml'
  }
}