package helpers

import javaposse.jobdsl.dsl.Job

class GitHelper {
  static def gitLabCredentialsId = '353f52e6-8580-466b-8efa-a3e228ff94e6'
  static def gitHubCredentialsId = 'c96237ea-9578-48ab-99a9-1ed228916423'
  static def linuxRepoReference = '/var/jenkins/reference_git_repos/backend_services'
  static def windowsRepoReference = 'C:\\Git\\backend-services'

  static void useGitLabBsRepo(Job job, String branchArg, String includedRegionsArg = '', String excludedRegionsArg = '', 
                              String repoReference = GitHelper.linuxRepoReference) {
    def projectRepo = 'https://gitlab.telerik.com/backendservices/backend-services.git'

    job.scm{
      git { 
        branch branchArg
        clean true

        remote {
          url projectRepo
          credentials GitHelper.gitLabCredentialsId
          reference repoReference
        }

        // Regions that are tracked for changes and trigger a build
        if (includedRegionsArg != '' || excludedRegionsArg != '' ) {
          configure { node -> 
            node / 'extensions' / 'hudson.plugins.git.extensions.impl.PathRestriction' << {
              includedRegions(includedRegionsArg);
              excludedRegions(excludedRegionsArg);
            }
          }
        }
      }
    }
  }
  
  static void useGitLabBsQARepo(Job job, String branchArg) {
    def projectRepo = 'https://gitlab.telerik.com/backendservices/bs-quality-assurance.git'

    job.scm{
      git { 
        branch branchArg
        clean true
        remote {
          url projectRepo
          credentials GitHelper.gitLabCredentialsId
        }
      }
    }
  }

  static void useGitHubRepo(Job job, String projectRepoArg, String branchArg) {
    job.scm {
      git {
        branch branchArg
        clean true
        remote {
          url projectRepoArg
          credentials GitHelper.gitHubCredentialsId
        }
      }
    }
  }
}
