def call( String branch , String repoUrl ) {

  pipeline {
    agent any
    tools {
        ansible 'ansible'
    }
    
    stages {
        stage('clone'){
              steps{
                    echo "Cloning repository..."
                      checkout([$class: 'GitSCM', branches: [[name: "*/${branch}"]],
                                userRemoteConfigs: [[url: repoUrl]]])
              }
          }
      
        stage ('user approval') {
            steps {
                input message: 'approval', ok: 'approval'
                echo "${WORKSPACE}"
            }
        }
        
        stage ('install tool') {
            steps {
                ansiblePlaybook credentialsId: '500d2a69-69ab-4df3-919f-8dcc27af1f77', 
                disableHostKeyChecking: true, installation: 'ansible', 
                inventory: 'aws_ec2.yml', playbook: 'nginx_playbook.yml'
            }
        }
        
       
        
    }
    post {
          success {
                  slackSend(channel: 'jenkins', message: "Build Successful: JOB-Name:- ${JOB_NAME} Build_No.:- ${BUILD_NUMBER} & Build-URL:- ${BUILD_URL}")
              }
          failure {
                  slackSend(channel: 'jenkins', message: "Build Failure: JOB-Name:- ${JOB_NAME} Build_No.:- ${BUILD_NUMBER} & Build-URL:- ${BUILD_URL}")
              }
         }
}
}
