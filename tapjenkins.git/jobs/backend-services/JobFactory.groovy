import helpers.*

class JobFactory {
  static createDefault(context, String jobName = '') {
    return context.job {
      name jobName
    }
  }

  static createLinux(context, String jobName = '', String label = 'ubuntu-common') {
  	def job = JobFactory.createDefault(context, jobName)

  	job.label label

  	job.configure NpmPackagesHelper.installation('LatestStable')

    job.publishers {
      wsCleanup() //Delete workspace after build.
    }

    return job
  }
  
    static createLinuxQA(context, String jobName = '', String label = 'ubuntu-common') {
  	def job = JobFactory.createDefault(context, jobName)

  	job.label label

  	job.configure NpmPackagesHelper.installation('LatestStable')

    return job
  }

  static createWindows(context, String jobName = '') {
    def job = JobFactory.createDefault(context, jobName)

    job.label 'windows'

    job.publishers {
      wsCleanup() //Delete workspace after build.
    }

    return job
  }

  static createDeploy(context, String jobName, String labelArg, String deployEnv) {

    def envDisplayName = deployEnv.toUpperCase()
    def runAllTestsJobName = 'Run_all_tests_for_' + envDisplayName + '_environment'

    def commonScriptWithoutGalaxy =
"""#!/bin/bash

set -e

printenv
ansible --version

export ANSIBLE_FORCE_COLOR=true
export PYTHONUNBUFFERED=1
"""
    def commonScript = commonScriptWithoutGalaxy + "ansible-galaxy install -p roles -r roles.yml --force"

    def deployScript = 
"""
ansible-playbook site.yml -i inventory/3dc_os --tags deploy -e \"tap_environment=${deployEnv} bs_deploy=true bs_version=\$PIPELINE_VERSION openstack_login_username=bot_testtap openstack_login_password=d93d34051a\"
"""
    if (deployEnv == 'uat' || deployEnv == 'live'){
      deployScript =
"""
ansible-playbook site-aws.yml -i inventory/aws --tags deploy -e \"tap_environment=${deployEnv} bs_deploy=true bs_version=\$PIPELINE_VERSION\"
"""
    }

    def job = JobFactory.createDefault(context, jobName)

    job.with {
      label labelArg

      parameters {
        stringParam('BS_Config_Branch', 'origin/master', 'From which branch to get the deployment script')
      }

      deliveryPipelineConfiguration('Environment - ' + envDisplayName, 'Deploy Packages')

      wrappers {
        buildName('${PIPELINE_VERSION}')
        colorizeOutput "xterm"
        timestamps()
      }

      if (deployEnv == 'uat'){
        wrappers {
          sshAgent "a1285a58-5b3d-4bb1-b6fd-3bd9584f583d"
        }
      }
      else if (deployEnv == 'live') {
        wrappers {
          sshAgent "9ebff33e-72c3-4465-bb24-efe16efff955"
        }
      }
      else {
        // workaround for the issue that sshAgent dsl does not support multiple keys
        configure { project ->
          project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
            'string'("ae19a478-b8a3-412c-93c6-3c18d74d76d4")
            'string'("8d27e655-4379-497b-b284-daca833668c8")
          }
        }
      }

      scm {
        git {
          branch '$BS_Config_Branch'
          clean true
          remote {
            url 'https://gitlab.telerik.com/platformdevops/tap-backendservices-machineconfig.git'
          }
        }
      }

      steps {
        shell(commonScript + deployScript)
      }

      configure DownstreamHelper.parameterized(runAllTestsJobName)
      configure DownstreamHelper.useCurrentBuildParams()
    }

    return job
  }
}