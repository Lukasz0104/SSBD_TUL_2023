pipeline {
    agent any
    environment { 
        backend = "${env.WORKSPACE}/backend"
        frontend = "${env.WORKSPACE}/frontend"
    }

    stages {
        stage('Clean') { 
            steps {
                sh "rm -rfd *"
            }
        }
        stage('Build') {
            steps {
                sh "ls -la ${env.WORKSPACE}"
                sh "cd  ${backend} && mvn clean install"
                sh "cd ${frontend} && npm run build"
                sh "ls -la ${backend}/target"
                sh "ls -la ${frontend}/dist"
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
