Write-Output("Clean build dir")
./gradlew clean
Write-Output("Build jar")
./gradlew build
Write-Output("Copy jar to host")
scp '.\build\libs\iot-assistant-rest.jar' pi@192.168.0.99:~
Write-Output("Execute installation script")
ssh -t pi@192.168.0.99 'sudo bash install.sh'
Write-Output("Installation finished")