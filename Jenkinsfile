pipeline {
     agent {
        label 'master' 
        }
     environment {
             FAVOURITE_FRUIT = 'tomato'
     }
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                script{
                    println("Running job ${env.JOB_NAME}")
                    def props = readProperties file: 'extravars.properties'
                    env.WEATHER = props.WEATHER
                     echo "The weather is ${WEATHER}"
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
                echo "I like to eat ${FAVOURITE_FRUIT} fruit"
            }
        }
    }
}
