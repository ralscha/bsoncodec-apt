cd bsoncodec-apt
call mvnw clean install
cd ..\bsoncodec-test
call mvnw clean test
pause
