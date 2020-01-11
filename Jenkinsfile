pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo "${DATE_GROOVY}"
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