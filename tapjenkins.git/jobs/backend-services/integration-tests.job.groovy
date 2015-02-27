import helpers.*

//Constants
def defaultBranch = '*/master'

def metadata_images = [id:'0', suiteTitle:"Metadata-images", suiteName:"Metadata - images"]
def geopoints = [id:'1', suiteTitle:"GeoPoints", suiteName:"GeoPoints"]
def oauth = [id:'2', suiteTitle:"aaOauthIntegration", suiteName:"OauthMochaTests"]
def deviceregistrations = [id:'3', suiteTitle:"DeviceRegistrations", suiteName:"DeviceRegistrations"]
def notifications = [id:'4', suiteTitle:"Notifications", suiteName:"NotificationsTests"]
def cloudcodedebugging = [id:'5', suiteTitle:"CloudCodeDebugging", suiteName:"CloudCodeDebugging"]
def date = [id:'6', suiteTitle:"Date", suiteName:"Dateeee"]
def cors = [id:'7', suiteTitle:"CORS", suiteName:"CORS"]
def emailtemplates = [id:'8', suiteTitle:"EmailTemplates", suiteName:"EmailTemplatesss"]
def adfsendpoints = [id:'9', suiteTitle:"AdfsEndpoints", suiteName:"AdfsEndpoints"]
def metadatacodelog = [id:'10', suiteTitle:"Metadata-codeLog", suiteName:"Metadata - codeLog"]
def metadataaccounts = [id:'11', suiteTitle:"Metadata-accounts", suiteName:"MetadataAccounts"]
def cloudcode = [id:'12', suiteTitle:"CloudCode", suiteName:"Cloud code test"]
def data = [id:'13', suiteTitle:"Data", suiteName:"Dataaaa"]
def metadatadatalinks = [id:'14', suiteTitle:"MetadataDataLinks", suiteName:"Metadata - Data Links"]
def datalink = [id:'15', suiteTitle:"DataLink", suiteName:"DataLinkTest"]
def cloudfunctions = [id:'16', suiteTitle:"Cloud functions", suiteName:"Cloud functions"]
def extrenalcalls = [id:'17', suiteTitle:"ExternalCalls", suiteName:"External calls from cloud code"]
def powerfields = [id:'18', suiteTitle:"PowerFields", suiteName:"PowerFields"]
def extrenalcallsusers = [id:'19', suiteTitle:"ExternalCallsUsers", suiteName:"External calls users type"]
def expand = [id:'20', suiteTitle:"Expand", suiteName:"Expand"]
def granularpermissions = [id:'21', suiteTitle:"GranularPermissions", suiteName:"GranularPermissions"]
def systemtypessecuritypolicy = [id:'22', suiteTitle:"SystemTypesSecurityPolicy", suiteName:"SystemTypesSecurityPolicy"]
def serverintegrationtests = [id:'23', suiteTitle:"Serverintegrationtests", suiteName:"Server integration tests"]
def metadatafields = [id:'24', suiteTitle:"Metadata-fields", suiteName:"Metadata - fields"]
def metadatapermissions = [id:'25', suiteTitle:"Metadata-permissions", suiteName:"Metadata - permissions"]
def metadataroles = [id:'26', suiteTitle:"Metadata-roles", suiteName:"Metadata - roles"]
def metadatatypes = [id:'27', suiteTitle:"Metadata-types", suiteName:"Metadata - types"]
def metadataapplications = [id:'28', suiteTitle:"Metadata-applications", suiteName:"Metadata-applications"]
def EverliveTAPIntegration = [id:'29', suiteTitle:"EverliveTAPIntegration", suiteName:"EverliveTAPIntegration"]
def licensing = [id:'30', suiteTitle:"Licensing", suiteName:"LicensingTest"]
def permissions = [id:'31', suiteTitle:"Permissions", suiteName:"TypePermissions"]
def accountslicensing = [id:'32', suiteTitle:"AccountsLicensing", suiteName:"AccountsLicensing"]
def files = [id:'33', suiteTitle:"Files", suiteName:"FilesMochaTests"]
def ACLOwner = [id:'34', suiteTitle:"ACLOwnerSpecialEndpoints", suiteName:"ACL/Owner"]
def authentication = [id:'35', suiteTitle:"Authentication", suiteName:"UsersTests"]
def indexes = [id:'36', suiteTitle:"Indexes", suiteName:"Indexes"]
def TFISAuthTests = [id:'37', suiteTitle:"TFISAuthTests", suiteName:"TFISAuthTests"]
def DataSecurityPolicy = [id:'38', suiteTitle:"DataSecurityPolicy", suiteName:"DataSecurityPolicy"]
def Responsive_Images = [id:'39', suiteTitle:"ResponsiveImages", suiteName:"Responsive_Images"]

def suites = []
suites.add(metadata_images)
suites.add(geopoints)
suites.add(oauth)
suites.add(deviceregistrations)
suites.add(notifications)
suites.add(cloudcodedebugging)
suites.add(date)
suites.add(cors)
suites.add(emailtemplates)
suites.add(adfsendpoints)
suites.add(metadatacodelog)
suites.add(metadataaccounts)
suites.add(cloudcode)
suites.add(data)
suites.add(metadatadatalinks)
suites.add(datalink)
suites.add(cloudfunctions)
suites.add(extrenalcalls)
suites.add(powerfields)
suites.add(extrenalcallsusers)
suites.add(expand)
suites.add(granularpermissions)
suites.add(systemtypessecuritypolicy)
suites.add(serverintegrationtests)
suites.add(metadatafields)
suites.add(metadatapermissions)
suites.add(metadataroles)
suites.add(metadatatypes)
suites.add(metadataapplications)
suites.add(EverliveTAPIntegration)
suites.add(licensing)
suites.add(permissions)
suites.add(accountslicensing)
suites.add(files)
suites.add(ACLOwner)
suites.add(authentication)
suites.add(indexes)
suites.add(TFISAuthTests)
suites.add(DataSecurityPolicy)
suites.add(Responsive_Images)

def suiteNamesString = ''

def DEV1 = [id:'0', shortName:'dev1', envName:'DEV1', viewName:'T1-DEV1', envConfig:'ExternalConfigDEV1', envConfigTapInt:'ExternalConfigTapIntDEV1', envConfigMetApp:'ExternalConfigMetAppDEV1']
def DEV3 = [id:'1', shortName:'dev3', envName:'DEV3', viewName:'T2-DEV3', envConfig:'ExternalConfigDEV3', envConfigTapInt:'ExternalConfigTapIntDEV3', envConfigMetApp:'ExternalConfigMetAppDEV3']
def SIT = [id:'2', shortName:'sit', envName:'SIT', viewName:'T3-SIT', envConfig:'ExternalConfigSIT', envConfigTapInt:'ExternalConfigTapIntSIT', envConfigMetApp:'ExternalConfigMetAppSIT']
def UAT = [id:'3', shortName:'uat', envName:'UAT', viewName:'T4-UAT', envConfig:'ExternalConfigUAT', envConfigTapInt:'ExternalConfigTapIntUAT', envConfigMetApp:'ExternalConfigMetAppUAT']
def LIVE = [id:'4', shortName:'live', envName:'LIVE', viewName:'T5-LIVE', envConfig:'ExternalConfigLIVE', envConfigTapInt:'ExternalConfigTapIntLIVE', envConfigMetApp:'ExternalConfigMetAppLIVE']

def environments = []
environments.add(DEV3)
environments.add(SIT)
environments.add(DEV1)
environments.add(UAT)
environments.add(LIVE)

//Create the jobs for the test suites
for (env in environments) {
  for (suite in suites) {
    def jobName = suite.suiteTitle +'-' + env.shortName
    suiteNamesString = jobName + ',' + suiteNamesString

    def suiteJob = null
    if( env.envName == 'UAT' )
    {
      suiteJob = JobFactory.createLinuxQA(this, jobName, "tap-uat-bastion-ssh-agent")
    }
    else if ( env.envName == 'LIVE' )
	{
	  suiteJob = JobFactory.createLinuxQA(this, jobName, "tap-live-bastion-ssh-agent")
	}
	else
    {
      suiteJob = JobFactory.createLinuxQA(this, jobName)
    }

    suiteJob.with {
      wrappers {
        colorizeOutput "xterm"
        timestamps()
      }

      throttleConcurrentBuilds {
        categories([env.envName])
      }
	
	if(suite.suiteTitle == 'EverliveTAPIntegration') {
	  steps {
        shell("""
printenv
cd IntegrationTests/
npm install
export JUNIT_REPORT_PATH=report.xml
echo "success" > log.txt & node node_modules/mocha/bin/mocha -R mocha-jenkins-reporter --globals global -t 100000 -r ./SpecialExternalConfigs/${env.envConfigTapInt} -g "${suite.suiteName}"
""")
      }
	
	}
	else if (suite.suiteTitle == 'Metadata-applications')
	{
      steps {
        shell("""
printenv
cd IntegrationTests/
npm install
export JUNIT_REPORT_PATH=report.xml
echo "success" > log.txt & node node_modules/mocha/bin/mocha -R mocha-jenkins-reporter --globals global -t 100000 -r ./SpecialExternalConfigs/${env.envConfigMetApp} -g "${suite.suiteName}"
""")
      }	
	}
	
	else {
      steps {
        shell("""
printenv
cd IntegrationTests/
npm install
export JUNIT_REPORT_PATH=report.xml
echo "success" > log.txt & node node_modules/mocha/bin/mocha -R mocha-jenkins-reporter --globals global -t 100000 -r ./${env.envConfig} -g "${suite.suiteName}"
""")
      }
	}
      
      publishers {
	      archiveArtifacts 'IntegrationTests/log.txt'
        archiveJunit 'IntegrationTests/report.xml'
      }
    }

    GitHelper.useGitLabBsQARepo(suiteJob, defaultBranch)
  //Old generation of 'Run_all_tests_suite'
    // job {
      // name 'Run_all_tests_for_' + env.envName + '_environment'
      // label "lightweight"

      // deliveryPipelineConfiguration('Environment - ' + env.envName, 'Run Integration Tests')
      
      // wrappers {
        // timestamps()
      // }

      // steps {
        // downstreamParameterized {
          // trigger(suiteNamesString, 'ALWAYS', true, 
            // ["buildStepFailure": "FAILURE", 
            // "failure": "FAILURE", 
            // "unstable": "UNSTABLE"])
        // }
      // }

      // // Manual trigger for promotion to next environment
      // if ( env.envName == 'SIT' ) {
        // configure DownstreamHelper.manual('Pipeline-UAT-Deploy')
      // } 
      // else if ( env.envName == 'UAT' ) {
        // configure DownstreamHelper.manual('Pipeline-LIVE-Deploy')
      // }
    // }
	//End of generation
	
	  job(type: BuildFlow) {
      name 'Run_all_tests_for_' + env.envName + '_environment'
      label "lightweight"
	  
	  deliveryPipelineConfiguration('Environment - ' + env.envName, 'Run Integration Tests')
	  
	  wrappers {
         timestamps()
       }
	  
		
	buildFlow("""
		def results = []
		
		parallel ( 
			{ ignore(FAILURE){ retry(3) {results[0] = build("AdfsEndpoints-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[1] = build("EverliveTAPIntegration-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[2] = build("Authentication-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[3] = build("CORS-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[4] = build("Cloud functions-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[5] = build("CloudCode-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[6] = build("CloudCodeDebugging-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[7] = build("Data-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[8] = build("DataLink-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[9] = build("Date-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[10] = build("DeviceRegistrations-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[11] = build("Expand-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(10) {results[12] = build("ExternalCalls-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[13] = build("ExternalCallsUsers-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[14] = build("Files-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[15] = build("GeoPoints-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[16] = build("Indexes-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[17] = build("Metadata-accounts-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[18] = build("Metadata-codeLog-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[19] = build("Metadata-fields-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[20] = build("Metadata-images-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[21] = build("Metadata-permissions-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[22] = build("Metadata-roles-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[23] = build("DataSecurityPolicy-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[24] = build("Metadata-types-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[25] = build("MetadataDataLinks-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[26] = build("Permissions-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[27] = build("PowerFields-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[28] = build("ResponsiveImages-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[29] = build("Serverintegrationtests-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[30] = build("SystemTypesSecurityPolicy-${env.shortName}")}}},
			{ ignore(FAILURE){ retry(3) {results[31] = build("TFISAuthTests-${env.shortName}")}}}
		)

		ignore(FAILURE){ retry(3) {results[32] = build("ACLOwnerSpecialEndpoints-${env.shortName}")}}
		ignore(FAILURE){ retry(3) {results[33] = build("AccountsLicensing-${env.shortName}")}}
		ignore(FAILURE){ retry(3) {results[34] = build("EmailTemplates-${env.shortName}")}}
		ignore(FAILURE){ retry(3) {results[35] = build("GranularPermissions-${env.shortName}")}}
		ignore(FAILURE){ retry(3) {results[36] = build("Licensing-${env.shortName}")}}
		ignore(FAILURE){ retry(3) {results[37] = build("Metadata-applications-${env.shortName}")}}
		ignore(FAILURE){ retry(3) {results[38] = build("Notifications-${env.shortName}")}}
		ignore(FAILURE){ retry(3) {results[39] = build("aaOauthIntegration-${env.shortName}")}}
		
		def finalResult = SUCCESS
		
		for (build in results) {
			finalResult = finalResult.combine(build.result)
		}
		
		build.state.setResult(finalResult)		
		""")
		
	   //Manual trigger for promotion to next environment
       if ( env.envName == 'SIT' ) {
         configure DownstreamHelper.manual('Pipeline-UAT-Deploy')
       } 
       else if ( env.envName == 'UAT' ) {
         configure DownstreamHelper.manual('Pipeline-LIVE-Deploy')
       }
     }
  }
  suiteNamesString = ''
}

//Create the views for the different environments (integration, test and live)
for (env in environments) {
  view(type: ListView) {
    name(env.viewName)
    description('This view contains all jobs for ' + env.envName + ' environment. You can trigger manually each test suite or use the \"Run_all_tests_for_' + env.envName + '_environment\" job in order to trigger all suites.')
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
        names(suite.suiteTitle +'-' + env.shortName)
        name('Run_all_tests_for_' + env.envName + '_environment')
      }
    }
  }
}