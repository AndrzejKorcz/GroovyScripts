pipeline {
     agent {
        label 'master' 
        }
           environment {
             DEVOPS_FOLDER = 'd:\\DevOps\\bin\\'
           }
                          
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
                echo "DevOps folder is ${DEVOPS_FOLDER} "
            }
        }
         
              
    }
}
