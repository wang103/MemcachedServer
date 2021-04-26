pipeline {
    agent { docker { image 'webratio/ant' } }
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
