pipeline {
  agent {
    node {
      label 'master'
      customWorkspace '/some/other/path'
    }
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
			     bat label: 'runAcmsCmpl', script: 'java -jar ./jar/ibmicmd.jar -c "ACMSCREATE PROJECT(J127404) ENV(DVP KORCZA03) FAILURE(*CONT) LISTING(*YES) CPYFFMTOPT(*NOCHK) SBMJOB(*NO) JOBD(ACMSSECMRP ACMSCTL) OUTQ(*USRPRF) REL(ICBSV710/ICBSV710/AIBINTMRO2)" "RUNCNVPGM PGM(PPTJ127404) DBLIB(POZAT01DB1)"'
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
            input {
                message "Should we continue?"
                ok "Yes, we should."
                submitter "andrzej,sławek"
                parameters {
                    string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
                }
            }
            steps {
                echo "Hello, ${PERSON}, nice to meet you."
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
