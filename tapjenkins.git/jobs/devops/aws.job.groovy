commonScript =
"""#!/bin/bash

set -e

printenv
ansible --version

export ANSIBLE_FORCE_COLOR=true
export PYTHONUNBUFFERED=1

ansible-galaxy install -r roles.yml -p roles --force
"""
environments = ['uat', 'live']
sshKeys = ['uat' : 'a1285a58-5b3d-4bb1-b6fd-3bd9584f583d',
            'live' : '9ebff33e-72c3-4465-bb24-efe16efff955']

commonProducts = ['infrastructure', 'appbuilder', 'platformportal',
            'backendservices', 'appmanager', 'appprototyper',
            'feedback', 'analytics', 'mobiletesting', 'screenbuilder']

// Use a similar syntax to add products for some environments only: 'live' : 
// products.clone() << 'product1' << 'product2'
productsPerEnvironment = ['uat' : commonProducts.clone(),
                          'live' : commonProducts.clone()]

def baseProductJob(String env, String fullProductName, boolean useDefaultSteps = true)
{
  this.job {
    name "${env}-${fullProductName}"
    label "aws-bastion-${env}"

    parameters {
      stringParam('Branch', 'origin/master', 'From which branch to get the machine config')
      stringParam('ExtraAnsibleArgs', "", "Aditional parameters for the ansible-playbook command. (ex: --skip-tags or --tags)")
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
          url "https://gitlab.telerik.com/platformdevops/tap-${fullProductName}-machineconfig.git/"
        }
      }
    }

    if (useDefaultSteps) {
      steps {
        shell(commonScript +
"""
ansible-playbook site-aws.yml -i inventory/aws -e \"tap_environment=${env}\" \$ExtraAnsibleArgs
""")
      }
    }
  }
}

for (env in environments) {
  baseProductJob(env, 'infrastructure')

  baseProductJob(env, 'appbuilder', false).with {

    parameters {
      stringParam('bpctooling_package', 'BpcTooling.[RELEASE].zip', 'BpcTooling file to deploy.')
    }

    steps {
      shell(commonScript +
"""
ansible-playbook site-aws.yml -i inventory/aws --vault-password-file=/opt/pass -e \"tap_environment=${env} bpctooling_package=\$bpctooling_package\" \$ExtraAnsibleArgs
""")
    }
  }

  baseProductJob(env, 'platformportal', false).with {
    steps {
      shell(commonScript +
"""
ansible-playbook site-aws.yml -i inventory/aws --vault-password-file=/opt/pass -e \"tap_environment=${env}\" \$ExtraAnsibleArgs
""")
    }
  }

  baseProductJob(env, 'backendservices')

  baseProductJob(env, 'appmanager', false).with {
    parameters {
      stringParam('DeployBuildUrl', "latest", "Url to the AppManager build to be used or *latest* to deploy the latest build.")
    }

    steps {
      shell(commonScript +
"""
ansible-playbook site-aws.yml -i inventory/aws --vault-password-file=/opt/pass -e \"nodejs_webserver_package_url='\$DeployBuildUrl' tap_environment=${env}\" \$ExtraAnsibleArgs
""")
    }
  }

  baseProductJob(env, 'appprototyper')

  baseProductJob(env, 'screenbuilder', false).with {

    steps {
      shell(commonScript +
"""
ansible-playbook site-aws.yml -i inventory/aws --vault-password-file=/opt/pass -e \"tap_environment=${env}\" \$ExtraAnsibleArgs
""")
    }
  }

  baseProductJob(env, 'feedback')

  baseProductJob(env, 'analytics', false).with {
    parameters {
      stringParam('DeployVersion', "latest", "Deploy version - use 'latest' for latest version")
    }

    steps {
      shell(commonScript +
"""
ansible-playbook site-aws.yml -i inventory/aws --vault-password-file=/opt/pass -e \"tap_environment=${env} deploy_version=\$DeployVersion\" \$ExtraAnsibleArgs
""")
    }
  }

  baseProductJob(env, 'mobiletesting', false).with {
    steps {
      shell(commonScript +
"""
ansible-playbook site-aws.yml -i inventory/aws --vault-password-file=/opt/pass -e \"tap_environment=${env} \" \$ExtraAnsibleArgs
""")
    }
  }

  job(type: BuildFlow) {
    name "_${env}-all"
    label 'master'

    parameters {
      stringParam('Branch', "origin/master", "From which branch to get the machine config")
      stringParam('ExtraAnsibleArgs', "", "Aditional parameters for the ansible-playbook command. (ex: --skip-tags or --tags)")
      booleanParam('RunParallel', false, "Run jobs in parallel?")
      for (product in productsPerEnvironment[env]) {
        booleanParam("run-${product}", true, "Run the ${env}-${product} job")
      }
    }

    String script = """
if (params["RunParallel"] == "true") {
  parallel (
"""
    for (product in productsPerEnvironment[env]) { script += """
    {
      if (params["run-${product}"] == "true") {
        build("${env}-${product}", Branch: params["Branch"], ExtraAnsibleArgs: params["ExtraAnsibleArgs"])
      }
    },
""" }

    script += """
  )
}
else {
"""

    for (product in productsPerEnvironment[env]) { script += """
    if (params["run-${product}"] == "true") {
      build("${env}-${product}", Branch: params["Branch"], ExtraAnsibleArgs: params["ExtraAnsibleArgs"])
    }
""" }

    script += "\n}"

    buildFlow(script)
  }
}

job(type: BuildFlow) {
  name "_uat-all-nightly"
  label 'master'

  triggers {
    cron "0 23 * * *"
  }

  String script = """
def result = SUCCESS
def tempBuild = null
"""

  for (product in productsPerEnvironment['uat']) { script += """
ignore(FAILURE) {
  retry(2) {
    tempBuild = build("uat-${product}")
  }
}
result = result.combine(tempBuild.result)
""" }

  script += "\nbuild.state.setResult(result)"

  buildFlow(script)
}

for (env in environments) {

  view(type: ListView) {
    name('DevOps ' + "${env}".toUpperCase())
    description("DevOps related jobs for the ${env} environment")
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

      if (env == 'uat') {
        name("_${env}-all-nightly")
      }

      name("_${env}-all")

      for (product in productsPerEnvironment[env]) {
        name("${env}-${product}")
      }

      name("${env}-backendservices-deploy")
    }
  }
}