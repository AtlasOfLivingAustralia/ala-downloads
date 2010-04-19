cd /home/tomcat/.hudson/jobs/bie-repository/workspace/bie-repository
svn update
mvn clean
mvn assembly:assembly -Dmaven.test.skip=true
cd target
nohup java -jar bie-repository-1.0-SNAPSHOT-jar-with-dependencies.jar 1036 3000 &