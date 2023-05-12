pipeline {
    agent any
    environment { 
        backend = "${env.WORKSPACE}/backend"
        frontend = "${env.WORKSPACE}/frontend"
        PATH = "/opt/java/payara6/bin:${env.PATH}"
    }
    tools {
        jdk "OpenJDK17"
    }

    stages {
        stage('Build') {
            steps {
                sh "cd  ${backend} && mvn clean install"
                sh "cd ${frontend} && npm install && npm run build"
                sh "ls -la ${backend}/target ${frontend}/dist"
                sh "echo $PATH"
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
