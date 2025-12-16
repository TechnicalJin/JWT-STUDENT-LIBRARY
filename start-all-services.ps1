# Start All Microservices - Fixed Configuration
# This script starts all services in the correct order with proper delays

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  JWT Student Library - Microservices Startup" -ForegroundColor Cyan
Write-Host "  Configuration: FIXED" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

$rootPath = "c:\Users\jinja\OneDrive\Desktop\Rest-Template SLA\JWT-STUDENT-LIBRARY"

# Function to start a service
function Start-Service {
    param(
        [string]$ServiceName,
        [string]$ServicePath,
        [int]$Port,
        [int]$WaitTime
    )
    
    Write-Host "[$ServiceName] Starting on port $Port..." -ForegroundColor Yellow
    
    $fullPath = Join-Path $rootPath $ServicePath
    
    if (Test-Path $fullPath) {
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$fullPath'; Write-Host 'Starting $ServiceName...' -ForegroundColor Cyan; mvn spring-boot:run"
        
        Write-Host "[$ServiceName] Waiting $WaitTime seconds for startup..." -ForegroundColor Gray
        Start-Sleep -Seconds $WaitTime
        
        # Check if port is listening
        $portCheck = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
        if ($portCheck) {
            Write-Host "[$ServiceName] ✓ Successfully started on port $Port" -ForegroundColor Green
        } else {
            Write-Host "[$ServiceName] ⚠ Port $Port not responding yet (may still be starting)" -ForegroundColor Yellow
        }
    } else {
        Write-Host "[$ServiceName] ✗ Path not found: $fullPath" -ForegroundColor Red
    }
    Write-Host ""
}

# Check if MySQL is running
Write-Host "[Pre-Check] Checking MySQL availability..." -ForegroundColor Cyan
$mysqlProcess = Get-Process mysqld -ErrorAction SilentlyContinue
if ($mysqlProcess) {
    Write-Host "[Pre-Check] ✓ MySQL is running" -ForegroundColor Green
} else {
    Write-Host "[Pre-Check] ⚠ MySQL process not detected - make sure MySQL is running!" -ForegroundColor Yellow
}
Write-Host ""

# Start services in order
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  Phase 1: Infrastructure Services" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

Start-Service -ServiceName "Eureka Server" -ServicePath "eureka-server" -Port 8761 -WaitTime 30
Start-Service -ServiceName "Config Server" -ServicePath "config-server" -Port 8888 -WaitTime 20

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  Phase 2: Business Services" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

Start-Service -ServiceName "Authentication Service" -ServicePath "authentication" -Port 8080 -WaitTime 25
Start-Service -ServiceName "Management Service" -ServicePath "management" -Port 8081 -WaitTime 20
Start-Service -ServiceName "Library Service" -ServicePath "library" -Port 8082 -WaitTime 20

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  Phase 3: API Gateway" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

Start-Service -ServiceName "API Gateway" -ServicePath "api-gateway" -Port 8083 -WaitTime 20

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "  Startup Complete!" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Service URLs:" -ForegroundColor Cyan
Write-Host "  Eureka Dashboard:      http://localhost:8761" -ForegroundColor White
Write-Host "  Config Server:         http://localhost:8888" -ForegroundColor White
Write-Host "  Authentication:        http://localhost:8080" -ForegroundColor White
Write-Host "  Management Service:    http://localhost:8081" -ForegroundColor White
Write-Host "  Library Service:       http://localhost:8082" -ForegroundColor White
Write-Host "  API Gateway:           http://localhost:8083" -ForegroundColor White
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "  1. Check Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "  2. Verify all services are registered (may take 30-60 seconds)" -ForegroundColor White
Write-Host "  3. Test authentication endpoint: http://localhost:8080/api/auth/login" -ForegroundColor White
Write-Host "  4. Check individual service health: http://localhost:<port>/actuator/health" -ForegroundColor White
Write-Host ""
Write-Host "Press any key to open Eureka Dashboard..." -ForegroundColor Green
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
Start-Process "http://localhost:8761"
