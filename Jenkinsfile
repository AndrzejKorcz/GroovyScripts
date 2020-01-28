pipeline {
  agent {
    node {
      label 'master'
      customWorkspace 'd:/DevOps/bin/'
    }
  }
    
  parameters {
     string(name: 'PROJECT', defaultValue: 'J127404', description: 'Acms task.')
     string(name: 'CNVPRGN', defaultValue: 'PPTJ127404', description: 'Main conversion program..')
     booleanParam(name: 'CNVPGM', defaultValue: true, description: 'Run the main conversion program?')
  }
  
  stages {

    stage('Build') {
        steps {
          echo 'Building..'
          script{
             println("Running job ${env.JOB_NAME}")
             def acms = '"' + "ACMSCREATE PROJECT(${params.PROJECT}) ENV(DVP KORCZA03) FAILURE(*CONT) LISTING(*YES) CPYFFMTOPT(*NOCHK) SBMJOB(*NO) JOBD(ACMSSECMRP ACMSCTL) OUTQ(*USRPRF) REL(ICBSV710/ICBSV710/AIBINTMRO2)" + '"'
             println("API acms: ${acms}") 

             def cmd
             if(Boolean.valueOf(params.CNVPGM)){ 
                def pgm = '"' + "RUNCNVPGM PGM(${CNVPRGN}) DBLIB(POZAT01DB1)" + '"'
                println("Run convert commend: ${pgm}") 
                cmd = "java -jar ./jar/ibmicmd.jar -c ${acms} ${pgm}"
                
             } else {
                cmd = "java -jar ./jar/ibmicmd.jar -c ${acms}"
             }
             
             println("Java command: ${cmd}")
			 bat label: 'runAcmsCmpl', script: "${cmd}"
          }
        }
    }
  
    stage('Test') {
      steps {
        echo 'Testing...'
      }
    }
	
    stage('Deploy') {
       steps {
          echo 'Deploying...'
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
