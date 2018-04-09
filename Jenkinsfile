#!groovy
@Library('Reform')
import uk.gov.hmcts.Ansible
import uk.gov.hmcts.Packager
import uk.gov.hmcts.RPMTagger
import uk.gov.hmcts.Versioner

properties(
  [[$class: 'GithubProjectProperty', projectUrlStr: 'https://git.reform.hmcts.net/probate/probate-sol-ccd-services.git'],
   pipelineTriggers([[$class: 'GitHubPushTrigger']]),

  parameters([
    string(description: 'Sol ccd url', defaultValue: 'http://betaDevaprobateapp01.reform.hmcts.net:4104', name: 'SOL_CCD_SERVICE_BASE_URL'),
    string(description: 'Idam url', defaultValue: 'http://betaDevAccidamAppLB.reform.hmcts.net', name: 'IDAM_BASE_URL'),
    string(description: 'PDF service url', defaultValue: 'http://devpdfservicelb.moneyclaim.reform.hmcts.net:4301', name: 'PDF_SERVICE_BASE_URL'),
    string(description: 'Fee service url', defaultValue: 'https://test.fees-register.reform.hmcts.net:4431', name: 'FEE_SERVICE_BASE_URL'),
    string(description: 'Evidence management url', defaultValue: 'https://api-gateway.dev.dm.reform.hmcts.net', name: 'EVIDENECE_MANAGEMENT_BASE_URL'),
  ])]
)

//@Library(['Reform', 'PROBATE'])
def ansible = new Ansible(this, 'probate')
def packager = new Packager(this, 'probate')
def versioner = new Versioner(this)

def rpmTagger
def app = "sol-ccd-service"
def artifactorySourceRepo = "probate-local"
def version
def serviceVersion
def serviceDockerVersion

node {
  try {
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

    stage('Test') {
      try {
        sh "./gradlew clean test"
      } finally {
        dest_dir = "${env.JENKINS_HOME}/reports/probate/sol-ccd-service/"
        sh "mkdir -p $dest_dir"
        sh "cp -R ./build/reports/* $dest_dir"

        publishHTML target: [
          alwaysLinkToLastBuild: true,
          reportDir            : "${env.JENKINS_HOME}/reports/probate/sol-ccd-service/tests/test",
          reportFiles          : "index.html",
          reportName           : "sol-ccd-service Test Report"
        ]

        publishHTML target: [
          alwaysLinkToLastBuild: true,
          reportDir            : "${env.JENKINS_HOME}/reports/probate/sol-ccd-service/jacoco",
          reportFiles          : "index.html",
          reportName           : "sol-ccd-service Code Coverage Report"
        ]
      }
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
      env.DOCKER_VERSION_TAG = serviceDockerVersion
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
        rpmTagger.tagDeploymentSuccessfulOn('dev')
      }

      stage('Tag Smoke Test success (Dev)') {
        sh "curl ${params.IDAM_BASE_URL}/health"
        sh "curl ${params.PDF_SERVICE_BASE_URL}/health"
        sh "curl ${params.FEE_SERVICE_BASE_URL}/health"
        sh "curl ${params.EVIDENECE_MANAGEMENT_BASE_URL}/health"
        sh "curl ${params.SOL_CCD_SERVICE_BASE_URL}/health"

        rpmTagger.tagTestingPassedOn('dev')
      }
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

  stage('Starting Integration job') {
    try {
      if ("develop" == "${env.BRANCH_NAME}") {
        build '../sol-ccd-services-integration-tests/develop'
      } else if ("master" == "${env.BRANCH_NAME}") {
        build '../sol-ccd-services-integration-tests/master'
      }
    } catch (err) {
      sh 'echo Integration test failed'
    }
  }
}
