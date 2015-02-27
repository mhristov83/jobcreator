import helpers.*

def jsDocToolRepo = 'https://gitlab.telerik.com/backendservices/jsdoc-to-markdown.git'
def jsDocToolBranch = 'origin/master'
def jsDocToolDir = 'jsdoc-to-markdown'
def jsSdkRepo = 'https://github.com/telerik/everlive-js-sdk.git'
def jsSdkBranch = 'origin/master'
def jsSdkDir = 'everlive-js-sdk'
def imagesRepo = 'https://github.com/telerik/backend-services-responsive-images-client.git'
def imagesBranch = 'origin/master'
def imagesDir = 'backend-services-responsive-images-client'
def bsRepo = 'https://gitlab.telerik.com/backendservices/backend-services.git'
def bsBranch = 'origin/master'
def bsDir = 'backend-services'

def job = JobFactory.createLinux(this, 'Build-API-Reference')

job.with {
    multiscm {
        git {
            branch jsDocToolBranch
            clean true

            remote {
                url jsDocToolRepo
                credentials GitHelper.gitLabCredentialsId
            }

            relativeTargetDir jsDocToolDir
        }

        git {
            branch '${jsSdkBranch}'
            clean true

            remote {
                url jsSdkRepo
                credentials GitHelper.gitHubCredentialsId
            }

            relativeTargetDir jsSdkDir
        }

        git {
            branch '${imagesBranch}'
            clean true

            remote {
                url imagesRepo
                credentials GitHelper.gitHubCredentialsId
            }

            relativeTargetDir imagesDir
        }

        git {
            branch '${bsBranch}'
            clean true

            remote {
                url bsRepo
                credentials GitHelper.gitLabCredentialsId
                reference GitHelper.linuxRepoReference
            }

            relativeTargetDir bsDir
        }
    }

    parameters {
        stringParam('bsBranch', bsBranch, 'The backend services branch for cloud code API reference')
        stringParam('jsSdkBranch', jsSdkBranch, 'The javascript SDK branch')
        stringParam('imagesBranch', imagesBranch, 'The images component branch')
    }

    steps {
        shell("""
cd jsdoc-to-markdown/tool
npm install
node BuildApiReference -f "../../everlive-js-sdk/everlive.js" -k "JavaScript SDK" -p "api-reference/JavascriptSDK"

node BuildApiReference -f "../../backend-services/Server/APIServer/Plugins/CodeExecution/EverliveApiBase.js, ../../backend-services/Server/APIServer/Plugins/CodeExecution/EverliveApiForCloudFunction.js, ../../backend-services/Server/APIServer/Plugins/CodeExecution/EverliveApiForContentType.js, ../../backend-services/Server/APIServer/ServerCode/Modules/EmailModule.js, ../../backend-services/Server/APIServer/ServerCode/Modules/HttpModule.js, ../../backend-services/Server/APIServer/ServerCode/Modules/SdkModule.js ../../backend-services/Server/APIServer/ServerCode/Modules/Integration/TwilioModule.js" -k "Cloud Code API" -p "api-reference/CloudCode"

node BuildApiReference -f "../../backend-services-responsive-images-client/src/js/everlive.images.js" -k "Responsive Images Javascript SDK" -p "api-reference/ResponsiveImages"

cd ../output/api-reference
zip -r js-api-reference.zip .
    """)
    }

    publishers {
        archiveArtifacts {
          pattern 'jsdoc-to-markdown/output/api-reference/*.zip'
          latestOnly true
        }
    }
}