web: java $JAVA_OPTS -Xmx256m -jar build/libs/*.jar --spring.profiles.active=prod,heroku
release: ./gradlew -Dliquibase.url=jdbc:mysql://be925c7634cb32:89992d9b@us-cdbr-east-04.cleardb.com/heroku_431d193730120a6 update -PrunList=main