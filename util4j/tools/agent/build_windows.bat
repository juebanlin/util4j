set dir=%cd%
cd %dir%
call mvn package
cd %dir%
copy .\target\agent-1.0.0-SNAPSHOT.jar ..\..\src\main\resources\tools\util4jAgent.jar
pause