
def concatenate(... p) {
    def s = ""
    p.each { s += it + " " }
    return s
}

def isNullOrEmpty(s) {
    return !s?.trim()
}

def cmdExec(command) {
    return bat(returnStdout: true, script: "${command}").trim()
}

def cmdCopyPropFromAcmsTask(map) {
    def templCpyToSmtf = map.props.get('template').get('cpytosmtf')
    def templMember = map.props.get('template').get('member')
    def javaJarIbmCmd = map.props.get('java').get('javajaribmcmd')
    def project = map.params.get('Project')
    def member = String.format(templMember, map.params.get('Developer'), project)
    def remoteFile = "${map.props.get('env').get('remote')}${project}.properties"
    def cpyToSmtfCmd  = '"' + String.format(templCpyToSmtf, member, remoteFile) + '"' 

    return concatenate(javaJarIbmCmd, cpyToSmtfCmd)
}

def copyPropFileFromIfs(map) {
    def javaJarIbmIfsCpyTxtFromIfs = map.props.get('java').get('javajaribmifscpytxtfromifs')
    def fileName = "${map.params.get('Project')}.properties"
    def remoteFile = "${map.props.get('env').get('remote')}${fileName}"
    def localFile = "${map.workspace}/${fileName}"

    return concatenate(javaJarIbmIfsCpyTxtFromIfs, "-r", remoteFile, "-l", localFile, "-c")
}

def acmsCompileTask(map) {
    def javaJarIbmCmd = map.props.get('java').get('javajaribmcmd')
    def templCnvPgmCmd = map.props.get('template').get('cnvpgmcmd')
    def templAcmsCompile = map.props.get('template').get('acmscompile')
    def developer = map.params.get('Developer')
    def mainCnvPgm = map.props.get('taskProperties').get('MAINCNVPGM') 
    def env = map.props.get('taskProperties').get('ENV')   
    def release = map.props.get('taskProperties').get('RELEASE')
    def project = map.params.get('Project')                     
    def acmsCompileCmd = '"' + String.format(templAcmsCompile, project, env, developer, release) + '"'
    def cnvPgmCmd = "" 
    if (!isNullOrEmpty(mainCnvPgm)) { 
          cnvPgmCmd = '"' + String.format(templCnvPgmCmd, mainCnvPgm, developer) + '"'
    }

    return concatenate(javaJarIbmCmd, acmsCompileCmd, cnvPgmCmd)
}

def getAcceptableCodeCoverage(percent) {
    if (isNullOrEmpty(percent)) {
       println("Minimum Acceptable Code Coverage is null or empty. Default is 0")
       percent = "0"
    }
    return percent
}

def prepareUnitTest(map) {
    def list = []
    def varMap = [:] 

    def javaJarIbmCmd = map.props.get('java').get('javajaribmcmd')
    def javaJarIbmIfsCpyByteFromIfs = map.props.get('java').get('javajaribmifscpybytefromifs')
    def javaJarRpgCc= map.props.get('java').get('javajarrpgcc') 
    def unitTest = map.props.get('taskProperties').get('UNITTEST')
    def percent = map.props.get('taskProperties').get('MINACCEPTCODECOVERAGE')
    def templRunUnitTest = map.props.get('template').get('rununittest')
    def templCodeCoverage = map.props.get('template').get('codecoverage')
    def remote = map.props.get('env').get('remote')
    def local = map.props.get('env').get('local')
    
    varMap.percent = externalMethod.getAcceptableCodeCoverage(percent)
    if (!isNullOrEmpty(unitTest)) {
        unitTest.tokenize(',').each {
            println "Unit test: ${it}" 
            varMap.ccFile = "${it}.cczip"
            varMap.unitTestCmd = '"' + String.format(templRunUnitTest, it) + '"'
            varMap.remoteFile = "${remote}${varMap.ccFile}"
            varMap.localFile = "${local}${varMap.ccFile}"
            varMap.codeCoverageCmd = '"' + String.format(templCodeCoverage, it, varMap.remoteFile) + '"'	 
		          
            list.add(concatenate(javaJarIbmCmd, varMap.unitTestCmd, varMap.codeCoverageCmd))
            list.add(concatenate(javaJarIbmIfsCpyByteFromIfs, "-r", varMap.remoteFile, "-l", varMap.localFile, "-c"))
            list.add(concatenate(javaJarRpgCc, "-f", varMap.localFile, "-p", varMap.percent))    
        }
    }
    return list
}    

def acmsPromoteTask(map) {
    def javaJarIbmSbmCmd = map.props.get('java').get('javajaribmsbmcmd')   
    def templAcmsPromote = map.props.get('template').get('acmspromote')
    def project = map.params.get('Project')
    def env = map.props.get('taskProperties').get('ENV')
    def developer = map.params.get('Developer')
    def release = map.props.get('taskProperties').get('RELEASE')                        
    def acmsPromoteTaskCmd = '"' + String.format(templAcmsPromote, project, env, developer, release) + '"'
    
    return concatenate(javaJarIbmSbmCmd, acmsPromoteTaskCmd)
}

def prepareIntegrationTest(map) {
    def list = []
    def javaJarIbmCmd = map.props.get('java').get('javajaribmcmd')
    def changeEnv = '"' + "SETPOZAT01" + '"'  //get it from acms properties
    def mainCnvPgm = map.props.get('taskProperties').get('MAINCNVPGM')
    def dbLib = map.props.get('taskProperties').get('DBLIB')  
    def testPgm = map.props.get('taskProperties').get('TESTPGM') 
    def templCnvPgmCmd = map.props.get('template').get('cnvpgmcmd')
    def templRunTest = map.props.get('template').get('runtest')
    def cnvPgmCmd = "" 
    if (!isNullOrEmpty(mainCnvPgm) && !isNullOrEmpty(dbLib)) { 
          cnvPgmCmd = '"' + String.format(templCnvPgmCmd, mainCnvPgm, dbLib) + '"'
    }
    list.add(concatenate(javaJarIbmCmd, changeEnv, cnvPgmCmd))
    
    def testPgmCmd = null
    if (!isNullOrEmpty(testPgm)) {
        testPgm.tokenize(',').each {
            println "Integration test: ${it}" 
            testPgmCmd = '"' + String.format(templRunTest, it) + '"'		          
            list.add(concatenate(javaJarIbmCmd, testPgmCmd)) 
        }
    }
  
    return list
}            


return this;