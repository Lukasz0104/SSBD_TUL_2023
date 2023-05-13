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
                sh "cd ${frontend} && npm install && ng build --configuration=production"
            }
        }
        stage('Deploy') {
            steps {
                sh "asadmin -u admin -W /var/lib/jenkins/.gfclient/pass redeploy --name eBok ${backend}/target/eBok.war"
                sh "sudo rm -rdf /var/www/*"
                sh "cp -vr ${frontend}/dist/frontend/* /var/www/"
            }
        }
    }
}
