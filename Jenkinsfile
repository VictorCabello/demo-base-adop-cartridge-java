#!/usr/bin/env groovy

node {

    stage('check java') {
        git credentialsId: 'adop-ldap-admin', url: 'ssh://jenkins@gerrit:29418/ExampleWorkspace/ExampleProject/jhpster-experiment'

        sh "java -version"
    }

    stage('clean') {
        sh "chmod +x mvnw"
        sh "./mvnw clean"
    }

    stage('install tools') {
        sh "./mvnw com.github.eirslett:frontend-maven-plugin:install-node-and-npm -DnodeVersion=v10.15.3 -DnpmVersion=6.9.0"
    }

    stage('npm install') {
        sh "./mvnw com.github.eirslett:frontend-maven-plugin:npm"
    }

    stage('backend tests') {
        try {
            sh "./mvnw verify"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/test-results/**/TEST-*.xml'
        }
    }

    stage('frontend tests') {
        try {
            sh "./mvnw com.github.eirslett:frontend-maven-plugin:npm -Dfrontend.npm.arguments='run test-ci'"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/test-results/TESTS-*.xml'
        }
    }

    stage('packaging') {
        sh "./mvnw verify -Pprod -DskipTests"
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }
   
    
    stage('quality analysis') {
        withSonarQubeEnv('ADOP Sonar') {
            sh "./mvnw sonar:sonar"
        }
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'target/jacoco', reportFiles: 'index.html', reportName: 'Jacoco Report', reportTitles: ''])
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'target/test-results/lcov-report', reportFiles: 'index.html', reportName: 'lcov Report', reportTitles: ''])

    }
}
