pipeline {
    agent any
    environment { 
        backend = "${env.WORKSPACE}/backend"
        frontend = "${env.WORKSPACE}/frontend"
    }
    tools {
        jdk "OpenJDK17"
    }

    stages {
        stage('Build') {
            steps {
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
