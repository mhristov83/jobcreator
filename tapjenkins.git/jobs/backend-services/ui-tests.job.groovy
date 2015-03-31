import helpers.*

//Constants
def defaultBranch = 'master'

def api_keys = [id:'0', suiteTitle:"TestinAutoApiKeys", suiteName:"api_keys_tests"]
def viewer = [id:'12', suiteTitle:"Viewer", suiteName:"viewer_tests"]




def suites = []
suites.add(api_keys)
suites.add(viewer)

def suiteNamesString = ''

def SIT = [id:'0', shortName:'sit', envName:'SIT', viewName:'U3-SIT', envConfig:'ExternalConfigSIT', envConfigTapInt:'ExternalConfigTapIntSIT']
def UAT = [id:'1', shortName:'uat', envName:'UAT', viewName:'U4-UAT', envConfig:'ExternalConfigUAT', envConfigTapInt:'ExternalConfigTapIntUAT']
def LIVE = [id:'2', shortName:'live', envName:'LIVE', viewName:'U5-LIVE', envConfig:'ExternalConfigLIVE', envConfigTapInt:'ExternalConfigTapIntLIVE']

def environments = []

environments.add(SIT)
environments.add(UAT)
environments.add(LIVE)

//Api and Master Keys per project per Env
def keys = [LIVE:[
                  Viewer:[
                        Api:'ApiKey-LIVE-Viewer',
                        Master:'MasterKey-LIVE-Viewer'
                  ],
                  Business:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Developer:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Trial:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Common:[
                        Api:'ApiKey-LIVE=Common',
                        Master:'MasterKey-LIVE=Common'
                  ],
      ],
      UAT:[
                  Viewer:[
                        Api:'ApiKey-UAT-Viewer',
                        Master:'MasterKey-UAT-Viewer'
                  ],
                  Business:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Developer:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Trial:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Common:[
                        Api:'ApiKey-UAT=Common',
                        Master:'MasterKey-UAT=Common'
                  ],
      ],
      SIT:[
                  Viewer:[
                        Api:'ApiKey-SIT-Viewer',
                        Master:'MasterKey-SIT-Viewer'
                  ],
                  Business:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Developer:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Trial:[
                        Api:'Gruumjdjdjdjdjdj',
                        Master:'GruMMUU<U'
                  ],
                  Common:[
                        Api:'ApiKey-SIT=Common',
                        Master:'MasterKey-SIT=Common'
                  ],
      ],
  ]

//Create the jobs for the test suites
for (env in environments) {
  for (suite in suites) {
    def suiteForCredentials;
    if((suite.id).toInteger()>9){

      suiteForCredentials=keys.${env.envName}.${suite.suiteTitle}
    }
    else{
      suiteForCredentials=keys.${env.envName}.Common
    }

    def jobName = env.envName+'-UI-TESTS-'+suite.suiteTitle
    suiteNamesString = jobName + ',' + suiteNamesString

    def suiteJob = null
    def label = 'ui-tests-runner'
    suiteJob = JobFactory.createWindowsQA(this, jobName, label)

    GitHelper.useGitLabBsQARepo(suiteJob, defaultBranch)

    suiteJob.with {
      wrappers {
        colorizeOutput "xterm"
        timestamps()
      }

      if(env.envName=='UAT') {
       steps {
        batchFile(($/
\\telerik.com\resources\TAP\QA\Tools\SikuliSet\tools\QRes\QRes.exe /x:1600 /y:1200
set TestRunner="C:\SikuliX\runScript.cmd"
set TestList="%WORKSPACE%\UITests\SIKULI\sikuli_tests\${suite.suiteName}.sikuli"
set TestEnv=https://testtap.telerik.com
set UserEmail=bsload@telerik.local
set apiUrl=testapi.everlive.com/v1/
set apiKey=suiteForCredentials.Api
set masterKey=suiteForCredentials.Master
set MetadataAppId=51e2cce0-6f37-11e4-832a-879d9d59ccef
set timeout=60
set tfisURL=https://testtfis.telerik.com/Authenticate/Wrapv0.9
set accountId=628d4475-9664-473a-a9e0-c0d68f16c49f
set dataLinkServerId=f43d2890-cc7b-11e4-9fde-256848cceb37
set contributorEmail=bscontributor@telerik.local
set viewerEmail=bsviewer@telerik.local
set noAccessEmail=bsnoaccess@telerik.local


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
set TestList="%WORKSPACE%\UITests\SIKULI\sikuli_tests\${suite.suiteName}.sikuli"
set TestEnv=https://sit-platform.telerik.rocks
set UserEmail=bsload@telerik.local
set apiUrl=sit-tap-bs.telerik.rocks/v1/
set apiKey=suiteForCredentials.Api
set masterKey=suiteForCredentials.Master
set MetadataAppId=42529940-ad17-11e4-967c-2d1ade8f9dca
set timeout=60
set tfisURL=https://localtfis.telerik.com/Authenticate/Wrapv0.9
set accountId=f584a6b1-7a72-4d33-af10-64c5165de424
set dataLinkServerId=d2f557f0-cc7d-11e4-a40b-e531d75568cb
set contributorEmail=bscontributor@telerik.local
set viewerEmail=bsviewer@telerik.local
set noAccessEmail=bsnoaccess@telerik.local

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
set TestList="%WORKSPACE%\UITests\SIKULI\sikuli_tests\${suite.suiteName}.sikuli"
set TestEnv=https://platform.telerik.com
set UserEmail=bsload@telerik.local
set apiUrl=api.everlive.com/v1/
set apiKey=suiteForCredentials.Api
set masterKey=suiteForCredentials.Master
set MetadataAppId=4cb190e0-6f36-11e4-b1c9-6de58479113e
set timeout=60
set tfisURL=https://tfis.telerik.com/Authenticate/Wrapv0.9
set accountId=e8ef4e6a-4be9-4675-a85e-b5e61d2793c5
set dataLinkServerId=5e5c02a0-c95c-11e4-86e6-9701422b62b9
set contributorEmail=bscontributor@telerik.local
set viewerEmail=bsviewer@telerik.local
set noAccessEmail=bsnoaccess@telerik.local

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
      name 'Run_all_UI_tests_for_' + env.envName + '_environment'
      label "ui-tests-runner"
    
    deliveryPipelineConfiguration('Environment - ' + env.envName, 'Run UI Tests')
    
    wrappers {
         timestamps()
       }
    
    
  buildFlow("""
    def results = []
    
    parallel (
    { ignore(FAILURE){ retry(3) {results[0] = build("${env.envName}-UI-TESTS-ApiKeys")}}}
    )
  
    parallel (
    { ignore(FAILURE){ retry(3) {results[11] = build("${env.envName}-UI-TESTS-Viewer")}}}
    )
    def finalResult = SUCCESS
    
    for (build in results) {
      finalResult = finalResult.combine(build.result)
    }
    
    build.state.setResult(finalResult)    
    """)
     }
  

suiteNamesString = ''
}