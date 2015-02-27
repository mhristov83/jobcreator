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

def environments = ['sit', 'sit1']

for (env in environments) {
  job {
    name "${env}-platformportal"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
      sshAgent "8d27e655-4379-497b-b284-daca833668c8"
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-platformportal-machineconfig.git'
        }
      }
    }

    def tap_deploy_app = ""
    if ( env == "sit" ) {
      tap_deploy_app = "TAP.New"
    } else if (env == "sit1") {
      tap_deploy_app = "TAP.SIT1"
    }

    steps {
      shell(commonScript +
"""
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a tap_deploy_app=${tap_deploy_app}\"
""")
    }
  }

  job {
    name "${env}-infrastructure"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('AdditionalAnsiblePlaybookParams', "", "Aditional parameters for the ansible-playbook command. \
(ex: --skip-tags or --tags). Available tags are: nginx, rabbitmq, logstash, rediscache, redisrouter, sentinel.")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
      sshAgent "8d27e655-4379-497b-b284-daca833668c8"
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-infrastructure-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a openstack_key_name=infrastructure\" \$AdditionalAnsiblePlaybookParams
""")
    }
  }

  job {
    name "${env}-appbuilder"
    label 'ubuntu-ansible'

    def extraArgs = ""
    if ( env == "sit" ) {
      extraArgs = '--skip-tags web-deploy'
    } else if (env == "sit1") {
      extraArgs = '--skip-tags go-agent'
    }

    parameters {
      stringParam('Branch', 'origin/master', 'From which branch to get the machine config')
      stringParam('ExtraAnsibleArgs', extraArgs, 'Pass --tags or --skip-tags here.')
      stringParam('tap_environment', env)
      stringParam('ans_package_version', 'latest', 'Version of the AppBuilder to deploy. Use *latest* or empty string to deploy the latest build.')
      stringParam('ans_bpctooling_source_url', '', 'URL of BpcTooling builds.')
      stringParam('ans_bpctooling_package', '', 'Name of BpcTooling file to deploy.')
      stringParam('ans_builderdaemon_source_url', '', 'URL of BuilderDaemon builds.')
      stringParam('ans_builderdaemon_package', '', 'Name of BuilderDaemon file to deploy.')
      stringParam('ans_linux_builder_queue', '', 'Linux BPC builder queue.')
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
      injectPasswords()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        // AppBuilder ssh key
        'string'("0e30c572-f6d8-42f2-b8a2-4e7fdcf79635")
        // Infrastructure ssh key
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-appbuilder-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
printenv | grep -i '^ans_' | sed 's/^ans_//;s/=/: /' > group_vars/all/build_vars.yml
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=\$tap_environment openstack_login_username=ab_testtap openstack_login_password=s6qz69vlxJ cdn_api_username=telerikCloud cdn_api_key=\$CDNApiKey\" \$ExtraAnsibleArgs
""")
    }
  }

  job {
    name "${env}-appmanager"
    label 'ubuntu-ansible'

    parameters {
      stringParam('DeployBuildUrl', "latest", "Url to the AppManager build to be used or *latest* to deploy the latest build.")
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("588e0f62-1df7-45eb-a175-756ce3cfb741")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-appmanager-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
ansible-playbook site.yml -i inventory/3dc_os -e \"nodejs_webserver_package_url='\$DeployBuildUrl' tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a\"
""")
    }
  }

  job {
    name "${env}-backendservices"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('ExtraAnsibleArgs', "", "Aditional parameters for the ansible-playbook command. (ex: --skip-tags or --tags)")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("ae19a478-b8a3-412c-93c6-3c18d74d76d4")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-backendservices-machineconfig.git'
        }
      }
    }

    steps {
      shell(commonScript +
"""
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a\" \$ExtraAnsibleArgs
""")
    }
  }

  job {
    name "${env}-appfeedback"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("b8e9ca7f-d5db-476b-bd83-62dc520e8dac")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }


    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-feedback-machineconfig.git'
        }
      }
    }

    def playbookScript =
"""
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a\"
"""

    def deployScript = ""

    if (env == "sit1") {
      def environmentUpperCase = "${env}".toUpperCase()
      def afStackName = "feedback.${environmentUpperCase}"
      def ttStackName = "TaskTracker.${environmentUpperCase}"
      deployScript =
"""
cd files/deployment
rake WORKSPACE=TAP STACK=${afStackName} ENVIRONMENT=${environmentUpperCase} PACKAGE_ID=feedback VERSION=v1.0.26 deploy
rake WORKSPACE=TAP STACK=${ttStackName} ENVIRONMENT=${environmentUpperCase} PACKAGE_ID=tasktracker VERSION=v0.0.148 deploy
"""
    }

    def appendScript = playbookScript + deployScript

    steps {
      shell(commonScript + appendScript)
    }
  }

  job {
    name "${env}-mobiletesting"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }

    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("56b32aa3-c190-43a6-8da8-f22a4863e9f8")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-mobiletesting-machineconfig.git'        
        }
      }
    }

    steps {
      shell(commonScript +
"""
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=tg_testtap openstack_login_password=DqYpoyAVXF\"
""")
    }
  }

  job {
    name "${env}-analytics"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('DeployVersion', "latest", "Deploy version - use 'latest' for latest version")
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
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a deploy_version=\$DeployVersion\"
""")
    }
  }

  job {
    name "${env}-appprototyper"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("1c63cdfe-7e0a-4919-92a2-b9e50aeefcdc")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-appprototyper-machineconfig.git'
        }
      }
    }

    def commonPlaybookScript =
"""
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a\"
"""

    def commonDeployScript = ""

    if (env == "sit1" || env == "sit") {
      def environmentUpperCase = "${env}".toUpperCase()
      def stackName = "Prototyping.${environmentUpperCase}"
      if (env == "sit") {
        stackName = "Prototyping.Amazon"
      }
      commonDeployScript =
"""
cd deployment
rake WORKSPACE=TAP STACK=${stackName} ENVIRONMENT=${environmentUpperCase} PACKAGE_ID=prototyping VERSION=1.0.189 deploy
"""
    }

    def commonAppendScript = commonPlaybookScript + commonDeployScript

    steps {
      shell(commonScript + commonAppendScript)
    }
  }

  job {
    name "${env}-screenbuilder"
    label 'ubuntu-ansible'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
    }

    wrappers {
      colorizeOutput "xterm"
      timestamps()
    }
    // workaround for the issue that sshAgent dsl does not support multiple keys
    configure { project ->
      project / 'buildWrappers' / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' / credentialIds << {
        'string'("1c63cdfe-7e0a-4919-92a2-b9e50aeefcdc")
        'string'("8d27e655-4379-497b-b284-daca833668c8")
      }
    }

    scm {
      git {
        branch '$Branch'
        clean true
        remote {
          url 'https://gitlab.telerik.com/platformdevops/tap-screenbuilder-machineconfig.git'
        }
      }
    }

    def commonPlaybookScript =
"""
ansible-playbook site.yml -i inventory/3dc_os -e \"tap_environment=${env} openstack_login_username=bot_testtap openstack_login_password=d93d34051a\"
"""

    def commonAppendScript = commonPlaybookScript

    steps {
      shell(commonScript + commonAppendScript)
    }
  }  

  job(type: BuildFlow) {
    name "_${env}-all-buildflow"
    label 'master'

    if (env == "sit1") {
      triggers {
        cron "0 1 * * *"
      }
    }
    if (env == "sit") {
      triggers {
        cron "0 23 * * *"
      }
    }

    def destroyEnvironmentDefault = env == "sit1"

    parameters {
      stringParam('AppBuilderVersion', "latest", "Version of the AppBuilder to deploy. Use *latest* or empty string to deploy the latest build.")
      stringParam('PlatformPortalVersion', "latest", "Version of the PlatformPortal to deploy. You can find available versions here: \nhttps://artifactsrvbg01.telerik.com/artifactory/simple/tap-portal-nuget/")
      booleanParam('DestroyEnvironment', destroyEnvironmentDefault, "Version of the PlatformPortal to deploy. You can find available versions here: \nhttps://artifactsrvbg01.telerik.com/artifactory/simple/tap-portal/deploy-packages/")
    }

    buildFlow("""
      def result = SUCCESS
      def tempBuild = null

      if(params["DestroyEnvironment"] == "true") {
        retry(3) {
          build("ansible-delete-openstack-group", DeleteGroup: "env_${env}:!an-sqlserver-1.${env}")
        }
      }

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_inf-${env}")
          }
          tempBuild = build("${env}-infrastructure")
        }
      }
      result = result.combine(tempBuild.result)

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_pp-${env}")
          }
          tempBuild = build("${env}-platformportal", DeployVersion: params["PlatformPortalVersion"])
        }
      }
      result = result.combine(tempBuild.result)

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_ab-${env}")
          }
          tempBuild = build("${env}-appbuilder", DeployVersion: params["AppBuilderVersion"])
        }
      }
      result = result.combine(tempBuild.result)

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_bs-${env}")
          }
          tempBuild = build("${env}-backendservices")
        }
      }
      result = result.combine(tempBuild.result)

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_am-${env}")
          }
          tempBuild = build("${env}-appmanager")
        }
      }
      result = result.combine(tempBuild.result)

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_af-${env}")
          }
          tempBuild = build("${env}-appfeedback")
        }
      }
      result = result.combine(tempBuild.result)

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_ap-${env}")
          }
          tempBuild = build("${env}-appprototyper")
        }
      }
      result = result.combine(tempBuild.result)

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_an-${env}:!an-sqlserver-1.${env}")
          }
          tempBuild = build("${env}-analytics")
        }
      }
      result = result.combine(tempBuild.result)

      ignore(FAILURE) {
        retry(2) {
          if(params["DestroyEnvironment"] == "true") {
            tempBuild = build("ansible-delete-openstack-group", DeleteGroup: "productenv_mt-${env}")
          }
          tempBuild = build("${env}-mobiletesting")
        }
      }
      result = result.combine(tempBuild.result)

      build.state.setResult(result)
    """)
  }
}

job {
  name "ansible-delete-openstack-group"
  label 'ubuntu-ansible'

  parameters {
    stringParam('DeleteGroup', "", "Group to delete")
  }

  wrappers {
    colorizeOutput "xterm"
    timestamps()
    sshAgent "8d27e655-4379-497b-b284-daca833668c8"
  }

  scm {
    git {
      branch 'master'
      clean true
      remote {
        url 'https://gitlab.telerik.com/platformdevops/ansible-common-tap-roles.git'
      }
    }
  }

  steps {
      shell(commonScriptWithoutGalaxy +
"""
cd utils
ansible-playbook delete-group.yml -i \"../openstack-inventory/inventory/nova.py\" -e \"delete_group=\$DeleteGroup openstack_login_username=bot_testtap openstack_login_password=d93d34051a\"
""")
  }
}

job {
  name "ansible-common-tap-roles-tests"
  label 'ubuntu-ansible'

  triggers {
      cron "0 4 * * *"
  }

  wrappers {
    colorizeOutput "xterm"
    timestamps()
    sshAgent "8d27e655-4379-497b-b284-daca833668c8"
  }

  scm {
    git {
      branch 'master'
      clean true
      remote {
        url 'https://gitlab.telerik.com/platformdevops/ansible-common-tap-roles.git'
      }
    }
  }

  steps {
    shell(commonScriptWithoutGalaxy +
"""
cd tests
ansible-playbook ubuntu14-common.yml -i inventory -e \"openstack_login_username=tap_infr_testtap openstack_login_password=cd18d35de0 openstack_key_name=infrastructure\"
ansible-playbook win2008-common.yml -i inventory -e \"openstack_login_username=tap_infr_testtap openstack_login_password=cd18d35de0 openstack_key_name=infrastructure\"
ansible-playbook win2012-common.yml -i inventory -e \"openstack_login_username=tap_infr_testtap openstack_login_password=cd18d35de0 openstack_key_name=infrastructure\"
""")
  }
}

view(type: ListView) {
  name('DevOps')
  description('DevOps related jobs')
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
      name('ansible-common-tap-roles-tests')
  }
}

view(type: ListView) {
  name('DevOps SIT1')
  description('DevOps related jobs')
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
      name('sit1-platformportal')
      name('sit1-infrastructure')
      name('sit1-appbuilder')
      name('sit1-appmanager')
      name('sit1-backendservices')
      name('sit1-backendservices-deploy')
      name('sit1-analytics')
      name('sit1-appfeedback')
      name('sit1-appprototyper')
      name('sit1-screenbuilder')
      name('sit1-mobiletesting')
      name('_sit1-all-buildflow')
  }
}

view(type: ListView) {
  name('DevOps SIT')
  description('DevOps related jobs')
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
      name('sit-platformportal')
      name('sit-infrastructure')
      name('sit-appbuilder')
      name('sit-appmanager')
      name('sit-backendservices')
      name('sit-backendservices-deploy')
      name('sit-analytics')
      name('sit-appfeedback')
      name('sit-appprototyper')
      name('sit-screenbuilder')
      name('sit-mobiletesting')
      name('_sit-all-buildflow')
  }
}
