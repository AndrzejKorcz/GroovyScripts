def config
def value
pipeline {
     agent {
        label 'master' 
        }
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                script{
                    println("Running job ${env.JOB_NAME}")
               
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
