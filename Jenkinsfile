pipeline {
    agent any
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
