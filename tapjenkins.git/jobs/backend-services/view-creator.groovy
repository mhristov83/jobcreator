import helpers.*

//Constants
def defaultBranch = 'master'

def DEV1 = [id:'0', shortName:'dev1', envName:'DEV1', envConfig:'ExternalConfigDev1', envConfigTapInt:'ExternalConfigTapIntDev1']
def DEV3 = [id:'1', shortName:'dev3', envName:'DEV3', envConfig:'ExternalConfigDev3', envConfigTapInt:'ExternalConfigTapIntDev3']
def SIT = [id:'2', shortName:'sit', envName:'SIT', envConfig:'ExternalConfigSIT', envConfigTapInt:'ExternalConfigTapIntSIT']
def UAT = [id:'3', shortName:'uat', envName:'UAT', envConfig:'ExternalConfigUAT', envConfigTapInt:'ExternalConfigTapIntUAT']
def LIVE = [id:'4', shortName:'live', envName:'LIVE', envConfig:'ExternalConfigLIVE', envConfigTapInt:'ExternalConfigTapIntLIVE']

def environments = []

environments.add(DEV1)
environments.add(DEV3)
environments.add(SIT)
environments.add(UAT)
environments.add(LIVE)

def typeOfTests =[]

def UI=[id:'0', shortName:'UI', envConfig:'ExternalConfigDev1', envConfigTapInt:'ExternalConfigTapIntDev1']
def INTEGRATION=[id:'1', shortName:'INT', envConfig:'ExternalConfigDev1', envConfigTapInt:'ExternalConfigTapIntDev1']

typeOfTests.add(UI)
typeOfTests.add(INTEGRATION)
//Create the views for the different environments (integration, test and live)

for(type in typeOfTests){
  if (type.shortName=='UI'){
    for(i=2; i<=4; i++){
      def env=environments[i]

      view(type:ListView){
        name(env.shortName+'-'+type.shortName)
        description('This view contains all jobs for ' + env.envName + ' environment. You can trigger manually each test suite or use the "Run_all_UI_tests_for_' + env.envName + '_environment" job in order to trigger all suites.')
        filterBuildQueue()
        filterExecutors()
        regex("${env.envName}-${type.shortName}-.+")
        columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
        }
        jobs{
          regex("${env.envName}-${type.shortName}-.+")
        }
      }
    }
  }

  else{
    view(type:ListView){
      name(env.envName+'-'+type.shortName)
      description('This view contains all jobs for ' + env.envName + ' environment. You can trigger manually each test suite or use the "Run_all_tests_for_' + env.envName + '_environment" job in order to trigger all suites.')
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
      jobs{
        regex(".+-${env.shortName}")
      }
    } 
  }
}