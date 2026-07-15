@REM Maven Wrapper Script for Windows
@echo off
setlocal

if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA_EXE=java"
)

set "WRAPPER_JAR=%~dp0.mvn\wrapper\maven-wrapper.jar"
set "PROJECT_DIR=%~dp0."

"%JAVA_EXE%" %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%PROJECT_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*

if ERRORLEVEL 1 exit /B 1
