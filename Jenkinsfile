pipeline {
   agent {
      label 'master' 
   }
   environment {
      DEVOPS_FOLDER = 'd:\\DevOps\\bin\\'
   }
	 
  stages {
  
    stage('Build') {
        steps {
                echo 'Building..'
                script{
                    println("Running job ${env.JOB_NAME}")
					dir('d:\\DevOps\\bin\\') {
					  bat label: 'runAcmsCmpl', script: 'java -jar d:/DevOps/bin/jar/ibmicmd.jar -c "RUNCNVPGM PGM(PPTJ127404) DBLIB(POZAT01DB1)"'
					}
					
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
  post {
    always {
      echo 'always runs regardless of the completion status of the Pipeline run'
    }
    success {
      echo 'step will run only if the build is successful'
    }
    failure {
      echo 'only when the Pipeline is currently in a "failed" state run, usually expressed in the Web UI with the red indicator.'
    }
    unstable {
      echo 'current Pipeline has "unstable" state, usually by a failed test, code violations and other causes, in order to run. Usually represented in a web UI with a yellow indication.'
    }
    changed {
      echo 'can only be run if the current Pipeline is running at a different state than the previously completed Pipeline'
    }
  }
}
