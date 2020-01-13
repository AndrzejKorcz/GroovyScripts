pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
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
                println("Running job ${env.JOB_NAME}")
                def config = readYaml(file:'config.yaml')
                def value = env.getProperty(config.myconfig.key)
                println("Value of property ${config.myconfig.key} is ${value}")
            }
        }
    }
}
