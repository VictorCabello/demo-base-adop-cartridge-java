import pluggable.scm.*;

SCMProvider scmProvider = SCMProviderHandler.getScmProvider("${SCM_PROVIDER_ID}", binding.variables)

// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"
def projectScmNamespace = "${SCM_NAMESPACE}"

// Variables
def projectNameKey = projectFolderName.toLowerCase().replace("/", "-")
def referenceAppgitRepo = "jhpster-experiment"
def regressionTestGitRepo =  "adop-cartridge-java-regression-tests"

// Jobs
def buildAppJob = pipelineJob(projectFolderName + "/JHipsterTest"){
        description("This job builds Java Spring reference application")
    wrappers {
        preBuildCleanup()
        injectPasswords()
        maskPasswords()
        sshAgent("adop-jenkins-master")
    }
    scm {
        git {
            remote {
                url(referenceAppGitUrl)
                credentials("adop-jenkins-master")
            }
            branch("*/master")
        }
    }
    environmentVariables {
        env('WORKSPACE_NAME', workspaceFolderName)
        env('PROJECT_NAME', projectFolderName)
    }
    label("java8")
    triggers {
        gerrit {
            events {
                refUpdated()
            }
            project(projectFolderName + '/' + referenceAppgitRepo, 'plain:master')
            configure { node ->
                node / serverName("ADOP Gerrit")
            }
        }
    }
    definition {
        cps {
            script(readFileFromWorkspace('cartridge/Jenkinsfile'))
            sandbox()
        }
    }
}
