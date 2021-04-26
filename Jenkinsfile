pipeline {
    agent { docker { image 'hammady/centos-jdk8-ant' } }
    stages {
        stage('build') {
            steps {
                sh 'echo "Running tests..."'
                sh 'ant test'

                sh 'echo "Starting build..."'
                sh 'ant'
            }
        }
    }
}
