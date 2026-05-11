pipeline {
    agent any

    parameters {
        string(name: 'TEST_EXEC_KEY', defaultValue: 'SUP-3', description: 'Xray Execution Key')
        string(name: 'TEST_KEY', defaultValue: 'SUP-4', description: 'Test Key to update')
    }

    environment {
        XRAY_BASE_URL = 'https://eu.xray.cloud.getxray.app'
        MAVEN_HOME = 'C:\\Users\\ADMIN\\Documents\\apache-maven-3.9.14\\bin'
    }

    stages {

        stage('Run Tests') {
            steps {
                bat """
                "%MAVEN_HOME%\\mvn" clean test ^
                -Dcucumber.filter.tags="@SUP-4" ^
                -Dcucumber.plugin="pretty,json:target\\cucumber.json"
                """
            }
        }

        stage('Generate Advanced Report') {
            steps {
                bat """
                "%MAVEN_HOME%\\mvn" test-compile exec:java ^
                -Dexec.mainClass="utils.ReportGenerator" ^
                -Dexec.classpathScope=test
                """
            }
        }

        stage('Create Xray JSON') {
            steps {
                powershell """
                \$filePath = "${env.WORKSPACE}\\target\\xray-result.json"

                \$tests = @()
                \$tests += @{
                    testKey = '${params.TEST_KEY}'
                    status = 'PASSED'
                }

                \$body = @{
                    testExecutionKey = '${params.TEST_EXEC_KEY}'
                    tests = \$tests
                } | ConvertTo-Json -Depth 5

                \$body | Out-File -FilePath \$filePath
                """
            }
        }

        stage('Upload to Xray') {
            steps {
                withCredentials([
                    string(credentialsId: 'XRAY_CLIENT_ID', variable: 'XRAY_CLIENT_ID'),
                    string(credentialsId: 'XRAY_CLIENT_SECRET', variable: 'XRAY_CLIENT_SECRET')
                ]) {
                    powershell """
                    \$authBody = @{
                        client_id = \$env:XRAY_CLIENT_ID
                        client_secret = \$env:XRAY_CLIENT_SECRET
                    } | ConvertTo-Json

                    \$token = Invoke-RestMethod `
                        -Uri "\$env:XRAY_BASE_URL/api/v2/authenticate" `
                        -Method Post `
                        -Body \$authBody `
                        -ContentType "application/json"

                    Invoke-RestMethod `
                        -Uri "\$env:XRAY_BASE_URL/api/v2/import/execution" `
                        -Method Post `
                        -Headers @{ Authorization = "Bearer \$token" } `
                        -InFile "${env.WORKSPACE}\\target\\xray-result.json"
                    """
                }
            }
        }
    }

    post {
        always {
            publishHTML(target: [
                reportDir: 'target/cucumber-html-reports/cucumber-html-reports',
                reportFiles: 'overview-features.html',
                reportName: 'Advanced Cucumber Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])
        }
    }
}