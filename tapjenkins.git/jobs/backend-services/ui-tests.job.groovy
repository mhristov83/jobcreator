import helpers.*

//Constants
def defaultBranch = 'master'

def api_keys = [id:'0', suiteTitle:"ApiKeys", suiteName:"api_keys_tests"]
def cloud_code = [id:'1', suiteTitle:"CloudCode", suiteName:"cloud_code_tests"]
def content_type = [id:'2', suiteTitle:"ContentType", suiteName:"content_type_tests"]
def downloads = [id:'3', suiteTitle:"Downloads", suiteName:"downloads_tests"]
def files = [id:'4', suiteTitle:"Files", suiteName:"files_tests"]
def project = [id:'5', suiteTitle:"Project", suiteName:"project_tests"]
def push_notifications = [id:'6', suiteTitle:"PushNotifications", suiteName:"push_notification_tests"]
def responsive_images = [id:'7', suiteTitle:"ResponsiveImages", suiteName:"responsive_images_tests"]
def time_open_project = [id:'8', suiteTitle:"TimeOpenProject", suiteName:"timeout_open_project_tests"]
def user = [id:'9', suiteTitle:"User", suiteName:"user_tests"]

def suites = []
suites.add(api_keys)
suites.add(cloud_code)
suites.add(downloads)
suites.add(files)
suites.add(project)
suites.add(push_notifications)
suites.add(responsive_images)
suites.add(time_open_project)
suites.add(user)

def suiteNamesString = ''

def SIT = [id:'0', shortName:'sit', envName:'SIT', viewName:'U3-SIT', envConfig:'ExternalConfigSIT', envConfigTapInt:'ExternalConfigTapIntSIT']
def UAT = [id:'1', shortName:'uat', envName:'UAT', viewName:'U4-UAT', envConfig:'ExternalConfigUAT', envConfigTapInt:'ExternalConfigTapIntUAT']
def LIVE = [id:'2', shortName:'live', envName:'LIVE', viewName:'U5-LIVE', envConfig:'ExternalConfigLIVE', envConfigTapInt:'ExternalConfigTapIntLIVE']

def environments = []

environments.add(SIT)
environments.add(UAT)
environments.add(LIVE)

//Create the jobs for the test suites
for (env in environments) {
  for (suite in suites) {
    def jobName = env.envName+'-UI-'+suite.suiteTitle +'Tests'
    suiteNamesString = jobName + ',' + suiteNamesString

    def suiteJob = null
    def label = 'ui-tests-runner'
    suiteJob = JobFactory.createWindows(this, jobName, label)

    suiteJob.with {
      wrappers {
        colorizeOutput "xterm"
        timestamps()
      }

      throttleConcurrentBuilds {
        categories([env.envName])
      }

      if(env.envName=='UAT') {
       steps {
        batchFile(($/
 \\telerik.com\resources\TAP\QA\Tools\SikuliSet\tools\QRes\QRes.exe /x:1600 /y:1200
 set TestRunner="C:\SikuliX\runScript.cmd"
 set TestList="%WORKSPACE%\UITests\SIKULI\sikuli_tests\'+suite.suiteName+'_tests.sikuli"
 set TestEnv=https://testtap.telerik.com
 set UserEmail=bsload@telerik.local
 set apiUrl=testapi.everlive.com/v1/
 set apiKey=MJsFfcOodnypQC63
 set masterKey=AGkXJ3j1oMSsMMFn14OA9hNn3Y2SWnI6
 set MetadataAppId=51e2cce0-6f37-11e4-832a-879d9d59ccef
 set timeout=60

 call %TestRunner% -r %TestList%

 findstr /m "errors=\"0\"" %WORKSPACE%\UITests\SIKULI\results\TEST-Report.xml 
 if not %ERRORLEVEL%==0 ( 
   exit /B 1
   )

 findstr /m "failures=\"0\"" %WORKSPACE%\UITests\SIKULI\results\TEST-Report.xml 
 if not %ERRORLEVEL%==0 ( 
   exit /B 1
   )
          /$))
      }
    }


    else if (env.envName=='SIT')
    {
      steps {
        batchFile(($/
\\telerik.com\resources\TAP\QA\Tools\SikuliSet\tools\QRes\QRes.exe /x:1600 /y:1200
set TestRunner="C:\SikuliX\runScript.cmd"
set TestList="%WORKSPACE%\UITests\SIKULI\sikuli_tests\'+suite.suiteName+'_tests.sikuli"
set TestEnv=https://testtap.telerik.com
set UserEmail=bsload@telerik.local
set apiUrl=testapi.everlive.com/v1/
set apiKey=MJsFfcOodnypQC63
set masterKey=AGkXJ3j1oMSsMMFn14OA9hNn3Y2SWnI6
set MetadataAppId=51e2cce0-6f37-11e4-832a-879d9d59ccef
set timeout=60

call %TestRunner% -r %TestList%

findstr /m "errors=\"0\"" %WORKSPACE%\UITests\SIKULI\results\TEST-Report.xml 
if not %ERRORLEVEL%==0 ( 
  exit /B 1
  )

findstr /m "failures=\"0\"" %WORKSPACE%\UITests\SIKULI\results\TEST-Report.xml 
if not %ERRORLEVEL%==0 ( 
  exit /B 1
  )
          /$))
      }
    }


    else if (env.envName=='LIVE')
    {
      steps{
       batchFile(($/
\\telerik.com\resources\TAP\QA\Tools\SikuliSet\tools\QRes\QRes.exe /x:1600 /y:1200
 set TestRunner="C:\SikuliX\runScript.cmd"
 set TestList="%WORKSPACE%\UITests\SIKULI\sikuli_tests\'+suite.suiteName+'_tests.sikuli"
 set TestEnv=https://platform.telerik.com
 set UserEmail=bsload@telerik.local
 set apiUrl=api.everlive.com/v1/
 set apiKey=UwoNtYbnakMRNucW
 set masterKey=BcA4dRTerYa5FsT4qNbCTF1grDbI6RDh
 set MetadataAppId=4cb190e0-6f36-11e4-b1c9-6de58479113e
 set timeout=60

 call %TestRunner% -r %TestList%

 findstr /m "errors=\"0\"" %WORKSPACE%\UITests\SIKULI\results\TEST-Report.xml 
 if not %ERRORLEVEL%==0 ( 
   exit /B 1
   )

 findstr /m "failures=\"0\"" %WORKSPACE%\UITests\SIKULI\results\TEST-Report.xml 
 if not %ERRORLEVEL%==0 ( 
   exit /B 1
   )
                /$))

     }
   }
   publishers {
      archiveJunit 'UITests\\SIKULI\\results\\*.xml'
    }
 }

}

job(type: BuildFlow) {
      name 'Run_all_tests_for_' + env.envName + '_environment'
      label "ui-tests-runner"
    
    deliveryPipelineConfiguration('Environment - ' + env.envName, 'Run Integration Tests')
    
    wrappers {
         timestamps()
       }
    
    
  buildFlow("""
    def results = []
    
    ignore(FAILURE){ retry(3) {results[0] = build("${env.shortName}-UI-ApiKeys-Tests")}}
    ignore(FAILURE){ retry(3) {results[1] = build("${env.shortName}-UI-CloudCode-Tests")}}
    ignore(FAILURE){ retry(3) {results[2] = build("${env.shortName}-UI-ContentType-Tests")}}
    ignore(FAILURE){ retry(3) {results[3] = build("${env.shortName}-UI-Downloads-Tests")}}
    ignore(FAILURE){ retry(3) {results[4] = build("${env.shortName}-UI-Files-Tests")}}
    ignore(FAILURE){ retry(3) {results[5] = build("${env.shortName}-UI-Project-Tests")}}
    ignore(FAILURE){ retry(3) {results[6] = build("${env.shortName}-UI-PushNotifications-Tests")}}
    ignore(FAILURE){ retry(3) {results[7] = build("${env.shortName}-UI-ResponsiveImages-Tests")}}
    ignore(FAILURE){ retry(3) {results[8] = build("${env.shortName}-UI-TimeOpenProject-Tests")}}
    ignore(FAILURE){ retry(3) {results[9] = build("${env.shortName}-UI-TimeOpenProject-Tests")}}
    
    def finalResult = SUCCESS
    
    for (build in results) {
      finalResult = finalResult.combine(build.result)
    }
    
    build.state.setResult(finalResult)    
    """)
     }
  

suiteNamesString = ''
}

//Create the views for the different environments (integration, test and live)
for (env in environments) {
  view(type: ListView) {
    name(env.viewName)
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
    for (suite in suites) {
      jobs {
        names(env.envName+'-UI-Tests-'+suite.suiteTitle)
        name('Run_all_tests_for_' + env.envName + '_environment')
      }
    }
  }
}