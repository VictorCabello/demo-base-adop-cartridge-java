#!/usr/bin/env groovy
pipeline {
    agent{ label 'java8' }
    stages {
    stage('check java') {
        steps{
            
        git credentialsId: 'adop-jenkins-master', url: 'ssh://jenkins@gerrit:29418/ExampleWorkspace/ExampleProject/jhpster-experiment'

        sh "java -version"
        sh "ls"
        }
    }

	
    stage('clean') {
        steps{
        sh "ls -la"
        sh "chmod +x mvnw"
        sh "./mvnw clean"
        }
    }
    stage('install tools') {
        steps{
        sh "./mvnw com.github.eirslett:frontend-maven-plugin:install-node-and-npm -DnodeVersion=v10.15.3 -DnpmVersion=6.9.0"
        }
    }

    stage('npm install') {
        steps{
        sh "./mvnw com.github.eirslett:frontend-maven-plugin:npm"
        }
    }

    stage('Static Code Analizis') {
        parallel {
               stage( 'frontend tests') {
                    steps{
                        script {
        try {
            sh "./mvnw com.github.eirslett:frontend-maven-plugin:npm -Dfrontend.npm.arguments='run test-ci'"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/test-results/TESTS-*.xml'
        }
        }
                    }
                }
               stage("quality analysis") {
                   steps{ 
                        script{
                try {
                    sh "yum install -y gcc-c++ make ;curl -sL https://rpm.nodesource.com/setup_6.x |  bash - "
                    withSonarQubeEnv('ADOP Sonar') {
                        sh "./mvnw  -Pprod clean verify  sonar:sonar"
                    }
                } catch(err) {
                    throw err
                } finally {
                    junit '**/target/test-results/**/TEST-*.xml'
                    
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'target/jacoco', reportFiles: 'index.html', reportName: 'Jacoco Report', reportTitles: ''])
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'target/test-results/lcov-report', reportFiles: 'index.html', reportName: 'lcov Report', reportTitles: ''])
                }
            }
                   }
                }
               stage("packaging") {
                    steps{
                    sh "./mvnw verify -Pprod -DskipTests"
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                    }
                }
        }
            
    }

    }
}
