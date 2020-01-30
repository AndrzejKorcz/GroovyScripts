pipeline {
  agent {
    node {
      label 'master'
    }
  }
  
  environment {
      DEVOPS_FOLDER = 'd:\\DevOps\\bin\\'
  }
    
  parameters {
     string(name: 'Project', defaultValue: 'J127404', description: 'Enter the name of the task in ACMS.')
     choice(name: 'InvokeConvPgm', choices:"No\nYes", description: "Run the main conversion program?" )
     choice(name: 'InvokeUnitTest', choices:"Yes\nNo", description: "Run unit tests?" )
  }
  
  stages {

    stage('Build') {
        steps {
          echo 'Building..'
          script {
             println("Running job ${env.JOB_NAME}")
             def acms = '"' + "ACMSCREATE PROJECT(${params.Project}) ENV(DVP KORCZA03) FAILURE(*CONT) LISTING(*YES) CPYFFMTOPT(*NOCHK) SBMJOB(*NO) JOBD(ACMSSECMRP ACMSCTL) OUTQ(*USRPRF) REL(ICBSV710/ICBSV710/AIBINTMRO2)" + '"'
             println("API acms: ${acms}") 

             def cmd = "java -jar ./jar/ibmicmd.jar -c ${acms}"
             
             if ("${params.InvokeConvPgm}" == "Yes") { 
               def ConvPgmName = 'PPTJ127404'
               def DBLib = 'POZAT01DB1'
               def pgm = '"' + "RUNCNVPGM PGM(${ConvPgmName}) DBLIB(${DBLib})" + '"'
               println("Run convert commend: ${pgm}") 
               cmd = "java -jar ./jar/ibmicmd.jar -c ${acms} ${pgm}"
             } 
 
             println("Java command: ${cmd}")
			 
			 dir("${DEVOPS_FOLDER}") {
			   bat label: 'runAcmsCmpl', script: "${cmd}"
			 }
          }
        }
    }
  
    stage('Test') {
      steps {
        echo 'Testing...'
        script{
          if ("${params.InvokeUnitTest}" == "Yes") {
		     def userInput = input(
                 id: 'userInput', message: 'Let\'s run some unit tests?', parameters: [
                 [$class: 'TextParameterDefinition', defaultValue: 'APB9017U', description: 'Unit test names', name: 'unittestname']
             ])
											 
		     println("Response input: ${userInput}")
			 
			 def unitTest = '"' + "RUCALLTST TSTPGM(${userInput}) RCLRSC(*ALWAYS)" + '"'
			 def cmd = "java -jar ./jar/ibmicmd.jar -c ${unitTest}"
			 
			 println("Java command: ${cmd}")  
			 
			 dir("${DEVOPS_FOLDER}") {
			   bat label: 'runUnitTest', script: "${cmd}"
			 }			 
          }
        }
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
