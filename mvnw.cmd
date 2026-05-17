@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set MAVEN_WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties"

@REM Download maven-wrapper.jar if it doesn't exist
if not exist %MAVEN_WRAPPER_JAR% (
    echo Downloading Maven Wrapper...
    for /f "tokens=2 delims==" %%a in ('findstr /r "wrapperUrl" %MAVEN_WRAPPER_PROPERTIES%') do set WRAPPER_URL=%%a
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar'"
)

@REM Run Maven via the wrapper jar
java.exe ^
    -classpath %MAVEN_WRAPPER_JAR% ^
    "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR:~0,-1%" ^
    org.apache.maven.wrapper.MavenWrapperMain ^
    %*

endlocal
