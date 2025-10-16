pipeline {
    agent any

    environment {
        // SonarQube - Already configured in your system
        SONAR_TOKEN = credentials('sonarqube-token')

        // Artifactory - Already configured in your system
        ARTIFACTORY_CREDS = credentials('artifactory-creds')

        // Application configuration
        APP_NAME = "customer-service"
        VERSION = "1.0.0"
        ARTIFACTORY_URL = "http://localhost:8082"
        REPO_KEY = "architech-rep"
        SONAR_HOST_URL = "http://localhost:9000"

        // Notifications - Update these based on your setup
        SLACK_CHANNEL = '#builds'
        ADMIN_EMAIL = 'babsmane@gmail.com'
        TEAM_EMAIL = 'jenkinsbabs434@gmail.com'  // Update this
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'develop',
                    url: 'https://github.com/babsmane/customer-app.git',
                    credentialsId: 'github-pat'
            }
        }

        stage('Build & Test') {
            steps {
                sh "mvn clean compile test"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh "mvn package -DskipTests"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Using your configured SonarQube server
                withSonarQubeEnv('Sonarqube-Server') {
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=${APP_NAME} \
                          -Dsonar.projectName=${APP_NAME} \
                          -Dsonar.java.binaries=target/classes \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                          -Dsonar.scm.provider=git \
                          -Dsonar.sourceEncoding=UTF-8
                    """
                }
            }
            post {
                success {
                   echo "✅ SonarQube analysis completed successfully"
                }
                failure {
                    echo "❌ SonarQube analysis failed - check SonarQube server logs"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    retry(2) {
                        timeout(time: 30, unit: 'MINUTES') {
                            def qualityGate = waitForQualityGate abortPipeline: false

                            if (qualityGate.status != 'OK') {
                                echo "Quality Gate status: ${qualityGate.status}"
                                if (qualityGate.status == 'ERROR') {
                                    error "Quality Gate failed: ${qualityGate.status}"
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Verify Artifactory Connectivity') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'artifactory-creds',
                        usernameVariable: 'ARTIFACTORY_USER',
                        passwordVariable: 'ARTIFACTORY_PASSWORD'
                    )]) {
                        sh """
                            echo "Testing Artifactory connection..."
                            curl -f -u \"${ARTIFACTORY_USER}:${ARTIFACTORY_PASSWORD}\" \
                                 \"${ARTIFACTORY_URL}/artifactory/api/system/ping\"
                            echo "✅ Artifactory connection successful"
                        """
                    }
                }
            }
        }

        stage('Publish to Artifactory') {
            steps {
                script {
                    // Find the WAR file
                    def warFile = sh(
                        script: 'find target -name "*.war" -type f | head -1',
                        returnStdout: true
                    ).trim()

                    if (warFile) {
                        echo "Found WAR file: ${warFile}"

                        withCredentials([usernamePassword(
                            credentialsId: 'artifactory-creds',
                            usernameVariable: 'ARTIFACTORY_USER',
                            passwordVariable: 'ARTIFACTORY_PASSWORD'
                        )]) {
                            sh """
                                # Upload using curl
                                echo "Uploading ${warFile} to Artifactory..."
                                curl -f -u \"${ARTIFACTORY_USER}:${ARTIFACTORY_PASSWORD}\" \
                                     -X PUT \
                                     \"${ARTIFACTORY_URL}/artifactory/${REPO_KEY}/${APP_NAME}/${VERSION}/$(basename ${warFile})\" \
                                     -T \"${warFile}\"

                                if [ \$? -eq 0 ]; then
                                    echo "✅ Successfully uploaded to Artifactory"
                                else
                                    echo "❌ Failed to upload to Artifactory"
                                    exit 1
                                fi
                            """
                        }
                    } else {
                        error "No WAR file found in target directory"
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                script {
                    echo "📦 Archiving build artifacts..."
                    archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    archiveArtifacts artifacts: 'target/site/**/*', fingerprint: true
                }
            }
        }

        stage('Cleanup') {
            steps {
                script {
                    echo "🧹 Cleaning up workspace..."
                    sh '''
                        # Clean Maven build artifacts
                        mvn clean || true

                        # Remove temporary files
                        rm -rf tmp/ || true
                        rm -rf out/ || true

                        # Clean up any large cache directories
                        find . -name "node_modules" -type d -exec rm -rf {} + 2>/dev/null || true
                        find . -name ".gradle" -type d -exec rm -rf {} + 2>/dev/null || true

                        echo "Workspace cleanup completed"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline succeeded! Artifact published to Artifactory."
            script {
                // Slack Notification - Only if configured
                try {
                    slackSend(
                        channel: "${SLACK_CHANNEL}",
                        color: 'good',
                        message: """✅ Pipeline SUCCESS
Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.GIT_BRANCH ?: 'unknown'}
Duration: ${currentBuild.durationString}
URL: ${env.BUILD_URL}"""
                    )
                } catch (Exception e) {
                    echo "Slack notification failed: ${e.message}"
                }

                // Email Notification
                emailext (
                    subject: "✅ SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                    body: """
                    <h2>Build Success ✅</h2>
                    <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                    <p><strong>Branch:</strong> ${env.GIT_BRANCH ?: 'unknown'}</p>
                    <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                    <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    <p><strong>Status:</strong> Artifact successfully published to Artifactory</p>
                    """,
                    to: "${TEAM_EMAIL}, ${ADMIN_EMAIL}",
                    replyTo: "${ADMIN_EMAIL}"
                )
            }
        }
        failure {
            echo "❌ Pipeline failed. Check logs for details."
            script {
                // Slack Notification - Only if configured
                try {
                    slackSend(
                        channel: "${SLACK_CHANNEL}",
                        color: 'danger',
                        message: """❌ Pipeline FAILED
Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.GIT_BRANCH ?: 'unknown'}
Duration: ${currentBuild.durationString}
URL: ${env.BUILD_URL}"""
                    )
                } catch (Exception e) {
                    echo "Slack notification failed: ${e.message}"
                }

                // Email Notification with logs
                emailext (
                    subject: "❌ FAILED: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                    body: """
                    <h2>Build Failed ❌</h2>
                    <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build:</strong> #${env.BUILD_NUMBER}</p>
                    <p><strong>Branch:</strong> ${env.GIT_BRANCH ?: 'unknown'}</p>
                    <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                    <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    <p><strong>Status:</strong> Please check the build logs for details</p>
                    """,
                    to: "${TEAM_EMAIL}, ${ADMIN_EMAIL}",
                    replyTo: "${ADMIN_EMAIL}",
                    attachLog: true
                )
            }
        }
        unstable {
            echo "⚠️ Pipeline unstable. Check test results."
            script {
                try {
                    slackSend(
                        channel: "${SLACK_CHANNEL}",
                        color: 'warning',
                        message: """⚠️ Pipeline UNSTABLE
Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.GIT_BRANCH ?: 'unknown'}
Duration: ${currentBuild.durationString}
URL: ${env.BUILD_URL}"""
                    )
                } catch (Exception e) {
                    echo "Slack notification failed: ${e.message}"
                }
            }
        }
        always {
            script {
                // Build Summary
                def duration = currentBuild.durationString
                def buildUrl = env.BUILD_URL
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER
                def gitBranch = env.GIT_BRANCH ?: 'unknown'
                def gitCommit = env.GIT_COMMIT ?: 'unknown'

                echo """
                ===================================
                🏗️  PIPELINE BUILD SUMMARY
                ===================================
                📋 JOB INFORMATION:
                   Name: ${jobName}
                   Build: #${buildNumber}
                   Status: ${currentBuild.result ?: 'SUCCESS'}

                ⏱️  TIMING:
                   Duration: ${duration}

                🔀 SOURCE CONTROL:
                   Branch: ${gitBranch}
                   Commit: ${gitCommit.take(8)}

                🌐 LINKS:
                   Build URL: ${buildUrl}
                ===================================
                """

                // Always archive test results
                junit 'target/surefire-reports/*.xml'
            }
        }
    }
}
