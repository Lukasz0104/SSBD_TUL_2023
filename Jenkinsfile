pipeline {
    agent any
    environment { 
        backend = "${env.WORKSPACE}/backend"
        frontend = "${env.WORKSPACE}/frontend"
    }

    stages {
        stage('Build') {
            steps {
                sh "export JAVA_HOME=/opt/java/jdk-17.0.6/"
                sh "cd  ${backend} && mvn clean install"
                sh "cd ${frontend} && npm run build"
                sh "ls -la ${backend}/target ${frontend}/dist"
            }
        }
        stage('Deploy') {
            steps {
                sh "asadmin redeploy --name eBok ${backend}/target/eBok.war"
                sh "rm -rdf /var/www/*"
                sh "cp -vr ${frontend}/dist/frontend/* /var/www/"
            }
        }
    }
}
