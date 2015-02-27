class NpmPackagesHelper {
  static Closure installation(String installationName) {
    return { project ->
      project / buildWrappers / 'jenkins.plugins.nodejs.tools.NpmPackagesBuildWrapper' << {
        nodeJSInstallationName(installationName);
      }
    }
  }
}

def commonScriptWithoutGalaxy =
"""#!/bin/bash

set -e

printenv
ansible --version

export ANSIBLE_FORCE_COLOR=true
export PYTHONUNBUFFERED=1
"""

def commonScript = commonScriptWithoutGalaxy + "ansible-galaxy install -p roles -r roles.yml --force"

/*************************************************
**************************************************
  This next section is for SIT and SIT1 (3DC_OS)
**************************************************
*************************************************/
def environments3DC_OS = ['sit', 'sit1']

for (env in environments3DC_OS) {
  
  job {
    name "${env}-deploy-analyticsweb"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('DeployVersion', "latest", "Deploy version - use 'latest' for latest version")
      stringParam('UpgradeSQLDatabase', "false", "Run SQL database upgrade - will run any database schema changes")
      stringParam('Nodes', "an-analyticsweb-1.${env},an-analyticsweb-2.${env}", "Deploy on these nodes")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("fd4415f2-c3a1-4c15-95fc-df99dbef081d")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-analytics-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
echo #
echo "################################## Start Deploy ##################################"
echo Start deploy of service: analyticsweb_deploy
echo Deploy Version: \$DeployVersion
echo Upgrade SQL Database: \$UpgradeSQLDatabase
echo Nodes: \$Nodes
ansible-playbook analyticsweb-deployer.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a deploy_version=\$DeployVersion upgrade_sql=\$UpgradeSQLDatabase\" -l \$Nodes
""")
    }
  }

  job {
    name "${env}-deploy-monitorweb"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('DeployVersion', "latest", "Deploy version - use 'latest' for latest version")
      stringParam('Nodes', "an-monitorweb-1.${env},an-monitorweb-2.${env}", "Deploy on these nodes")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("fd4415f2-c3a1-4c15-95fc-df99dbef081d")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-analytics-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
echo #
echo "################################## Start Deploy ##################################"
echo Start deploy of service: monitorweb_deploy
echo Deploy Version: \$DeployVersion
echo Nodes: \$Nodes
ansible-playbook monitorweb-deployer.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a deploy_version=\$DeployVersion\" -l \$Nodes
""")
    }
  }
  
  job {
    name "${env}-deploy-service"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('DeployVersion', "latest", "Deploy version - use 'latest' for latest version")
      stringParam('DeployService', "deploy_accountrestrictions,deploy_jobservice,deploy_messagequeuebackup,deploy_monitorprocessing,deploy_workflowengine", "Witch service should be deployed")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("fd4415f2-c3a1-4c15-95fc-df99dbef081d")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-analytics-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
echo #
echo "################################## Start Deploy ##################################"
echo Start deploy of service: \$DeployService
echo Deploy Version: \$DeployVersion
ansible-playbook service-deployer.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a deploy_version=\$DeployVersion\" --tags=\$DeployService
""")
    }
  }
}


/*************************************************
**************************************************
  This next section is for UAT and LIVE (AWS) 
**************************************************
*************************************************/
def environmentsAWS = ['uat','live']
sshKeys = ['uat' : 'a1285a58-5b3d-4bb1-b6fd-3bd9584f583d',
           'live' : '9ebff33e-72c3-4465-bb24-efe16efff955']
			
for (env in environmentsAWS) {
  
  job {
    name "${env}-deploy-analyticsweb"
    label "aws-bastion-${env}"

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('DeployVersion', "", "Deploy version - version should be found in TeamCity")
      stringParam('UpgradeSQLDatabase', "false", "Run SQL database upgrade - will run any database schema changes")
      stringParam('Nodes', "an-analyticsweb-1.${env},an-analyticsweb-2.${env}", "Deploy on these nodes")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
      sshAgent sshKeys[env]
    }
  
    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-analytics-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
echo #
echo "################################## Start Deploy ##################################"
echo Start deploy of service: analyticsweb_deploy
echo Deploy Version: \$DeployVersion
echo Upgrade SQL Database: \$UpgradeSQLDatabase
echo Nodes: \$Nodes
ansible-playbook analyticsweb-deployer.yml -i inventory/aws --vault-password-file=/opt/pass -e \"tap_environment=${env} deploy_version=\$DeployVersion upgrade_sql=\$UpgradeSQLDatabase\" -l \$Nodes
""")
    }
  }

  job {
    name "${env}-deploy-monitorweb"
    label "aws-bastion-${env}"

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('DeployVersion', "", "Deploy version - version should be found in TeamCity")
      stringParam('Nodes', "an-monitorweb-1.${env},an-monitorweb-2.${env}", "Deploy on these nodes")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
      sshAgent sshKeys[env]
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-analytics-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
echo #
echo "################################## Start Deploy ##################################"
echo Start deploy of service: monitorweb_deploy
echo Deploy Version: \$DeployVersion
echo Nodes: \$Nodes
ansible-playbook monitorweb-deployer.yml -i inventory/aws --vault-password-file=/opt/pass -e \"tap_environment=${env} deploy_version=\$DeployVersion\" -l \$Nodes
""")
    }
  }
  
  job {
    name "${env}-deploy-service"
    label "aws-bastion-${env}"

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('DeployVersion', "", "Deploy version - version should be found in TeamCity")
      stringParam('DeployService', "deploy_accountrestrictions,deploy_jobservice,deploy_messagequeuebackup,deploy_monitorprocessing,deploy_workflowengine", "Witch service should be deployed")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
      sshAgent sshKeys[env]
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-analytics-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
echo #
echo "################################## Start Deploy ##################################"
echo Start deploy of service: \$DeployService
echo Deploy Version: \$DeployVersion
ansible-playbook service-deployer.yml -i inventory/aws --vault-password-file=/opt/pass -e \"tap_environment=${env} deploy_version=\$DeployVersion\" --tags=\$DeployService
""")
    }
  }
}



/*************************************************
**************************************************
  This next section is for setup Jenkins Views 
**************************************************
*************************************************/

view(type: ListView) {
  name('Analytics')
  description('Analytics related jobs')
  filterBuildQueue()
  filterExecutors()
  columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
  jobs {
      name('sit-deploy-analyticsweb')
      name('sit-deploy-monitorweb')
      name('sit-deploy-service')
      
      name('sit1-deploy-analyticsweb')
      name('sit1-deploy-monitorweb')
      name('sit1-deploy-service')
      
      name('uat-deploy-analyticsweb')
      name('uat-deploy-monitorweb')
      name('uat-deploy-service')
      
      name('live-deploy-analyticsweb')
      name('live-deploy-monitorweb')
      name('live-deploy-service')
  }
}