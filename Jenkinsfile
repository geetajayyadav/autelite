pipeline {
    agent any

    parameters {
        string(name: 'TEST_EXEC_KEY', defaultValue: 'SUP-3', description: 'Xray Execution Key')
        string(name: 'TEST_KEY', defaultValue: 'SUP-4', description: 'Test Key')
    }

    environment {
        XRAY_BASE_URL = 'https://eu.xray.cloud.getxray.app'
        MAVEN_HOME = 'C:\\Users\\ADMIN\\Documents\\apache-maven-3.9.14\\bin'
        PROJECT_PATH = "${WORKSPACE}"
    }

    stages {

        // =========================
        // STEP 1 - RUN TESTS
        // =========================
        stage('Run Tests') {
            steps {
                bat """
                cd %PROJECT_PATH%

                "%MAVEN_HOME%\\mvn" clean test ^
                -Dcucumber.filter.tags="@SUP-4" ^
                -Dcucumber.plugin="pretty,json:target\\cucumber.json"
                """
            }
        }

        // =========================
        // STEP 2 - GENERATE REPORT
        // =========================
        stage('Generate Advanced Report') {
            steps {
                bat """
                cd %PROJECT_PATH%

                "%MAVEN_HOME%\\mvn" test-compile exec:java ^
                -Dexec.mainClass="utils.ReportGenerator" ^
                -Dexec.classpathScope=test
                """
            }
        }

        // =========================
        // STEP 3 - UPLOAD TO XRAY (FIXED)
        // =========================
        stage('Upload to Xray') {
            steps {
                withCredentials([
                    string(credentialsId: 'XRAY_CLIENT_ID', variable: 'XRAY_CLIENT_ID'),
                    string(credentialsId: 'XRAY_CLIENT_SECRET', variable: 'XRAY_CLIENT_SECRET')
                ]) {
                    powershell """
                    Write-Host "🔐 Authenticating with Xray..."

                    \$authBody = @{
                        client_id = \$env:XRAY_CLIENT_ID
                        client_secret = \$env:XRAY_CLIENT_SECRET
                    } | ConvertTo-Json

                    \$token = Invoke-RestMethod `
                        -Uri "\$env:XRAY_BASE_URL/api/v2/authenticate" `
                        -Method Post `
                        -Body \$authBody `
                        -ContentType "application/json"

                    Write-Host "📤 Uploading Cucumber JSON to Xray..."

                    Invoke-RestMethod `
                        -Uri "\$env:XRAY_BASE_URL/api/v2/import/execution/cucumber" `
                        -Method Post `
                        -Headers @{ Authorization = "Bearer \$token" } `
                        -InFile "target\\cucumber.json" `
                        -ContentType "application/json"
                    """
                }
            }
        }
    }

    // =========================
    // POST ACTIONS
    // =========================
    post {
        always {

            // Archive files
            archiveArtifacts artifacts: 'target/*.json, target/*.html', allowEmptyArchive: true

            // Publish HTML Report
            publishHTML(target: [
                reportDir: 'target/cucumber-html-reports/cucumber-html-reports',
                reportFiles: 'overview-features.html',
                reportName: 'Advanced Cucumber Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])
        }

        success {
            echo "✅ BUILD SUCCESS — REPORT + XRAY UPDATED"
        }

        failure {
            echo "❌ BUILD FAILED"
        }
    }
}