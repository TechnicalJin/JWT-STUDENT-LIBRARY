# Verify Configuration Setup
Write-Host "Configuration Verification Script" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

$rootPath = "c:\Users\jinja\OneDrive\Desktop\Rest-Template SLA\JWT-STUDENT-LIBRARY"
$allGood = $true

Write-Host "Checking Configuration Files..." -ForegroundColor Yellow
Write-Host ""

# Check Authentication Service
Write-Host "[1] Authentication Service" -ForegroundColor Cyan
if (Test-Path "$rootPath\authentication\src\main\resources\application.properties") {
    Write-Host "  OK - application.properties exists" -ForegroundColor Green
} else {
    Write-Host "  ERROR - application.properties missing" -ForegroundColor Red
    $allGood = $false
}

if (Test-Path "$rootPath\authentication\src\main\resources\application-cloud.properties") {
    Write-Host "  WARNING - application-cloud.properties should be deleted" -ForegroundColor Red
    $allGood = $false
} else {
    Write-Host "  OK - application-cloud.properties removed" -ForegroundColor Green
}

# Check Config Server
Write-Host ""
Write-Host "[2] Config Server" -ForegroundColor Cyan
if (Test-Path "$rootPath\config-server\src\main\resources\config\application.yml") {
    Write-Host "  OK - common application.yml exists" -ForegroundColor Green
} else {
    Write-Host "  ERROR - common application.yml missing" -ForegroundColor Red
    $allGood = $false
}

if (Test-Path "$rootPath\config-server\src\main\resources\config\authentication-service.yml") {
    Write-Host "  OK - authentication-service.yml exists" -ForegroundColor Green
} else {
    Write-Host "  ERROR - authentication-service.yml missing" -ForegroundColor Red
    $allGood = $false
}

# Check Eureka
Write-Host ""
Write-Host "[3] Eureka Server" -ForegroundColor Cyan
if (Test-Path "$rootPath\eureka-server\src\main\resources\application.yml") {
    Write-Host "  OK - application.yml exists" -ForegroundColor Green
} else {
    Write-Host "  ERROR - application.yml missing" -ForegroundColor Red
    $allGood = $false
}

Write-Host ""
Write-Host "=================================" -ForegroundColor Cyan
if ($allGood) {
    Write-Host "ALL CHECKS PASSED!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Ready to start services with: .\start-all-services.ps1" -ForegroundColor White
} else {
    Write-Host "ISSUES FOUND - Please fix before starting" -ForegroundColor Red
}
Write-Host "=================================" -ForegroundColor Cyan
