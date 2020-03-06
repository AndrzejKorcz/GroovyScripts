import java.text.SimpleDateFormat
// import groovy.json.JsonBuilder
import groovy.json.*

def getTimestamp() {
  def dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss")
  def date = new Date()
  return dateFormat.format(date)   
}

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
    def listCommand = []
    def listReport = []
    def varMap = [:] 
    def outMap = [:]

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
         varMap.jsonFile = "${map.workspace}/${it}.json"
         varMap.unitTestCmd = '"' + String.format(templRunUnitTest, it) + '"'
         varMap.remoteFile = "${remote}${varMap.ccFile}"
         varMap.localFile = "${local}${varMap.ccFile}"
         varMap.codeCoverageCmd = '"' + String.format(templCodeCoverage, it, varMap.remoteFile) + '"'	 
		       
         listCommand.add(concatenate(javaJarIbmCmd, varMap.unitTestCmd, varMap.codeCoverageCmd))
         listCommand.add(concatenate(javaJarIbmIfsCpyByteFromIfs, "-r", varMap.remoteFile, "-l", varMap.localFile, "-c"))
         listCommand.add(concatenate(javaJarRpgCc, "-f", varMap.localFile, "-p", varMap.percent, "-s", varMap.jsonFile))             
         listReport.add("${it},${varMap.jsonFile}")  
       }
       outMap.put("command", listCommand)
       outMap.put("report", listReport)
    }
    return outMap
}   

def reportListToMap(list) {
    map = [:]
    if (!isNullOrEmpty(list)) {
      int index = 0
      final utName = 1
      final utPath = 2
      list.tokenize(',').each {      
        index ++
            switch(index) {
                case utName: 
                  map.put("unitTestName", it)
                  break
                case utPath: 
                  map.put("unitTestPath", it)
                  break
            }
      }     
    } 
  return map  
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

def metricsDefault() {
    map = [:]
    map.put("project", "")
    map.put("start", 0)
    map.put("prepare", false)
    map.put("build", false)
    map.put("unittest", false)
    map.put("deploy", false)
    map.put("test", false)
    map.put("report", false)
    map.put("end", 0)
    map.put("utreport", null)
    return map 
}

def mapToJson(map) {
    def json = JsonOutput.toJson(map)
    return json.toString()
}

def sendMail(map) {
    def javaJarIbmSbmCmd = map.props.get('java').get('javajaribmsbmcmd')   
    def templSndMail = map.props.get('template').get('sndmail')

    def project = map.params.get('Project')
    def developer = map.params.get('Developer')
    def email = map.props.get('env').get('mail')
    // def subject = "Jenkins Build ${map.status}: for ${project} on ${env} ${developer}" 
    def subject = "Jenkins Build ${map.status} for ${project}" 
    def note = "Details: ${map.buildUrl}"
    def sendMailCmd = '"' + String.format(templSndMail, email, subject, note) + '"'   
    println sendMailCmd
    return concatenate(javaJarIbmSbmCmd, sendMailCmd)
}

def postData(map) {
    def javaJarPost = map.props.get('java').get('javajarpost')  
    return concatenate(javaJarPost, "-j", map.json, "-u", map.url)
}


return this;