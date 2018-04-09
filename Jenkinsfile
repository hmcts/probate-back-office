#!groovy
@Library('Reform')
import uk.gov.hmcts.Ansible
import uk.gov.hmcts.Packager
import uk.gov.hmcts.RPMTagger
import uk.gov.hmcts.Versioner

properties(
  [[$class: 'GithubProjectProperty', projectUrlStr: 'https://github.com/hmcts/probate-sol-ccd-services.git'],
  parameters([ 
      string(description: 'Store RPM variable for branches than master or develop (other than "no" stores rpm)', defaultValue: 'no', name: 'store_rpm')
  ]),
  pipelineTriggers([[$class: 'GitHubPushTrigger']])]
)

//@Library(['Reform', 'PROBATE'])
def ansible = new Ansible(this, 'probate')
def packager = new Packager(this, 'probate')
def versioner = new Versioner(this)

def rpmTagger
def app = "sol-ccd-service"
def artifactorySourceRepo = "probate-local"

node {
  try {
    def version
    def serviceVersion
    def serviceDockerVersion
    def storeRPMToArtifactory = false

    if(store_rpm != 'no' || "master"  == "${env.BRANCH_NAME}" || "develop"  == "${env.BRANCH_NAME}") {
        storeRPMToArtifactory = true
    }


    stage('Checkout') {
      deleteDir()
      checkout scm
    }

    if ("master" != "${env.BRANCH_NAME}") {
      newAppVersion = "-SNAPSHOT"
      if("develop"  != "${env.BRANCH_NAME}") {
          newAppVersion = "-${env.BRANCH_NAME}-SNAPSHOT"
      }
      echo "${newAppVersion}"
      stage('Add SNAPSHOT using SED') {
        sh '''
            sed -i '/version/ s/"/${newAppVersion}"/2' build.gradle
                '''
      }
    }

    stage('Build') {
      sh "./gradlew clean checkstyleTest"
    }

    try {
      stage('sonar & findbugs check') {
        sh '''./gradlew sonarqube
	            ./gradlew findbugsMain
	            ./gradlew findbugsTest'''
        dest_dir = "${env.JENKINS_HOME}/reports/probate/sol-ccd-service/"
        sh "mkdir -p $dest_dir"
        sh "cp ./build/reports/findbugs/main.html $dest_dir"
        sh "cp ./build/reports/findbugs/test.html $dest_dir"

        publishHTML target: [
          alwaysLinkToLastBuild: true,
          reportDir            : "${env.JENKINS_HOME}/reports/probate/sol-ccd-service",
          reportFiles          : "main.html",
          reportName           : "sol-ccd-service find Bugs Report"
        ]
      }
    } catch (err) {
      sh '''
                    echo "Ignore sonar scanner error"
                    echo $err
                    '''
    }

    try {
      stage('OWASP') {
        sh "./gradlew clean build"
      }
    } catch (err) {
      dest_dir = "${env.JENKINS_HOME}/reports/probate/sol-ccd-service/"
      sh "mkdir -p $dest_dir"
      sh "cp ./build/reports/dependency-check-report.html $dest_dir"
      publishHTML target: [
        alwaysLinkToLastBuild: true,
        reportDir            : "${env.JENKINS_HOME}/reports/probate/sol-ccd-service",
        reportFiles          : "dependency-check-report.html",
        reportName           : "sol-ccd-service owasp dependency Report"
      ]
      sh '''
                    echo "Ignore sonar scanner error"
                    echo $err
                    '''
    }

    stage('Package (JAR)') {
      versioner.addJavaVersionInfo()
      sh "./gradlew bootRepackage installDist"
    }

    stage('Package (Docker)') {
      if ("develop" == "${env.BRANCH_NAME}") {
        serviceDockerVersion = 'develop'
        dockerImage imageName: 'probate/sol-ccd-service', tags: [serviceDockerVersion]
      } else if ("master" == "${env.BRANCH_NAME}") {
        serviceDockerVersion = 'latest'
        dockerImage imageName: 'probate/sol-ccd-service'
      } else {
        serviceDockerVersion = dockerImage imageName: 'probate/sol-ccd-service'
      }

      sh "echo Docker version is: $serviceDockerVersion"
    }

    if(storeRPMToArtifactory) {
      stage('Package (RPM)') {
        serviceVersion = packager.javaRPM(app, 'build/libs/sol-ccd-service-$(./gradlew -q printVersion).jar',
            'springboot', 'src/main/resources/application.yml')
        sh "echo $serviceVersion"
        version = "{probate_sol_ccd_buildnumber: ${serviceVersion} }"
        sh "echo $version"
        def rpmName = packager.rpmName(app, serviceVersion)
        sh "echo $rpmName"
        rpmTagger = new RPMTagger(this, app, rpmName, artifactorySourceRepo)
        packager.publishJavaRPM(app)
      }
    }


    if ("develop" == "${env.BRANCH_NAME}") {
      stage('Install (Dev)') {
        ansible.runInstallPlaybook(version, 'dev')
      }

      stage('Deploy (Dev)') {
        ansible.runDeployPlaybook(version, 'dev')
      }

      stage('Tag Deploy success (Dev)') {
        rpmTagger.tagDeploymentSuccessfulOn('dev')
      }

      /*
      stage('Tag Smoke Test success (Dev)') {
        rpmTagger.tagTestingPassedOn('dev')
      } */
    }

  } catch (err) {
    slackSend(
      channel: '#probate-jenkins',
      color: 'danger',
      message: "${env.JOB_NAME}:  <${env.BUILD_URL}console|Build ${env.BUILD_DISPLAY_NAME}> has FAILED probate Sol CCD service")
    throw err

  }

  stage ('Starting Integration job') {
    build job: 'sol-ccd-services-integration-tests'
  }
}
