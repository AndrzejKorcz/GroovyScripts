
def concatenate(... p) {
    def s = ""
    p.each { s += it + " " }
    s
}

def isNullOrEmpty(s) {
    !s?.trim()
}

def cmdExec(command) {
    return bat(returnStdout: true, script: "${command}").trim()
}


def cmdCopyPropFromAcmsTask(map) {
    def cpyToSmtf  = '"' + String.format(map.t_cpytosmtf, map.member, map.remotefile) + '"'  
    concatenate(map.javajaribmcmd, cpyToSmtf)
}

def copyPropFileFromIfs(map) {
    concatenate(map.javajaribmifscpytxtfromifs, "-r", map.remotefile, "-l", map.localfile, "-c")
}

def runAcmsCompileTask(map) {
    def acmsCompileCmd = '"' + map.acmscompilecmd + '"'
    def cnvPgmCmd = "" 
    if (!isNullOrEmpty(map.maincnvpgm)) { 
          cnvPgmCmd = '"' + String.format(map.t_cnvpgmcmd, map.maincnvpgm, map.developer) + '"'
    }
    concatenate(map.javajaribmcmd, acmsCompileCmd, cnvPgmCmd)
}

def getAcceptableCodeCoverage(percent) {
    if (isNullOrEmpty(percent)) {
       println("Minimum Acceptable Code Coverage is null or empty. Default is 0")
       percent = "0"
    }
    percent
}

def prepareUnitTest(map) {
    def list = []
    def varmap = [:]  //later!
    def unitTest = map.unittest
    def percent = externalMethod.getAcceptableCodeCoverage(map.percent)
    def ccFile = null 
    def unitTestCmd = null  
    def codeCoverageCmd = null
    def remoteFile = null
    def localFile = null

    if (!isNullOrEmpty(unitTest)) {
        unitTest.tokenize(',').each {
            println "Unit test: ${it}" 
            ccFile = "${it}.cczip"
            unitTestCmd = '"' + String.format(map.t_rununittest, it) + '"'
            remoteFile = "${map.remote}${ccFile}"
            localFile = "${map.local}${ccFile}"
            codeCoverageCmd = '"' + String.format(map.t_codecoverage, it, remoteFile) + '"'	 
		          
            list.add(concatenate(map.javajaribmcmd, unitTestCmd, codeCoverageCmd))
            list.add(concatenate(map.javajaribmifscpybytefromifs, "-r", remoteFile, "-l", localFile, "-c"))
            list.add(concatenate(map.javajarrpgcc, "-f", localFile, "-p", percent))    
        }
    }

    return list
}                         
              	 
              //  def runCodeCoverageReport = "${props.java.javajar} ${props.java.jarrpgcc} -f ${localFile} -p ${percent}" 
                //  echo = bat label: 'Get code coverage raport', returnStdout: true, script: "${runCodeCoverageReport}"


return this;