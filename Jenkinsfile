#!groovy
@Library('Reform')
import uk.gov.hmcts.Ansible
import uk.gov.hmcts.Packager
import uk.gov.hmcts.RPMTagger
import uk.gov.hmcts.Versioner

properties(
  [[$class: 'GithubProjectProperty', projectUrlStr: 'https://git.reform.hmcts.net/probate/sol-ccd-service.git'],
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

    stage('Checkout') {
      deleteDir()
      checkout scm
    }

    if ("master" != "${env.BRANCH_NAME}") {
      stage('Develop Branch SNAPSHOT') {
        sh '''
                    sed 's/version = "0.0.1"/version = "0.0.1-SNAPSHOT"/' build.gradle > build_gradle
                    mv build_gradle build.gradle
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

    stage('Package (RPM)') {
      if ("master" != "${env.BRANCH_NAME}") {
        serviceVersion = packager.javaRPM(app, 'build/libs/sol-ccd-service-0.0.1-SNAPSHOT.jar',
          'springboot', 'src/main/resources/application.yml')
      } else {
        serviceVersion = packager.javaRPM(app, 'build/libs/sol-ccd-service-0.0.1.jar',
          'springboot', 'src/main/resources/application.yml')
      }

      sh "echo $serviceVersion"
      version = "{probate_sol_ccd_buildnumber: ${serviceVersion} }"
      sh "echo $version"
    }

    if ("develop" == "${env.BRANCH_NAME}") {
      def rpmName = packager.rpmName(app, serviceVersion)
      sh "echo $rpmName"
      rpmTagger = new RPMTagger(this, app, rpmName, artifactorySourceRepo)
      packager.publishJavaRPM(app)

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

    if ("master" == "${env.BRANCH_NAME}") {
      def rpmName = packager.rpmName(app, serviceVersion)
      sh "echo $rpmName"
      rpmTagger = new RPMTagger(this, app, rpmName, artifactorySourceRepo)
      packager.publishJavaRPM(app)
      /* removed auto deploy to test
      stage('Install (Test)') {
        ansible.runInstallPlaybook(version, 'test')
      }

      stage('Deploy (Test)') {
        ansible.runDeployPlaybook(version, 'test')
      }

      stage('Tag Deploy success (Test)') {
        rpmTagger.tagDeploymentSuccessfulOn('test')
      }
      */
      /*
      stage('Tag Smoke Test success (test)') {
        rpmTagger.tagTestingPassedOn('test')
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
