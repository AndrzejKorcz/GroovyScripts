def config
def value
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                script{
                    config = readYaml(file:'config.yaml')
               
                      }
            }
        }
        stage('Test') {
            steps {
                 echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying..'
                
            }
        }
    }
}
