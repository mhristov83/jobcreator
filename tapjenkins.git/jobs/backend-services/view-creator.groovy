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
def INTEGRATION=[id:'1', shortName:'Integration', envConfig:'ExternalConfigDev1', envConfigTapInt:'ExternalConfigTapIntDev1']

typeOfTests.add(UI)
typeOfTests.add(INTEGRATION)

//Create a parent view to include all other views
view(type:NestedView){
  name('TESTS-MH')
  description('This view contains all UI and Inegration tests')
  filterBuildQueue()
  filterExecutors()
  //Create child views
  views{
    view(type:ListView){
      name("Execute all tests")
      description('This view contains the jobs for running all tests for all environments')
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
        regex(".*Execute.*")
      }

      for(type in typeOfTests){
      //Create views for UI tests
      if (type.shortName=='UI'){
        for(i=2; i<=4; i++){
          def env=environments[i]
          view(type:ListView){
            name(env.envName+'-'+type.shortName)
            description('This view contains all jobs for ' + env.envName + ' environment. You can trigger manually each test suite or use the "Run_all_UI_tests_for_' + env.envName + '_environment" job in order to trigger all suites.')
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
              regex("(Run_all_UI_tests_for_${env.envName}|${env.envName}-${type.shortName}-.*)")
            }
          }
        }
      }

      //Create views for Integration test
      else{
        for(env in environments){
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
              regex("(Run_all_tests_for_|.+-${env.shortName})")
            }
          }
        } 
      }
    }
  }
}