# JWT-STUDENT-LIBRARY Phase 1 Startup Script
# Starts all microservices in correct order with updated port configuration
# Authentication: 8080 | Management: 8081 | Library: 8082 | API Gateway: 8083

Write-Host "========================================" -ForegroundColor Green
Write-Host "   JWT-STUDENT-LIBRARY Phase 1 Startup" -ForegroundColor Green  
Write-Host "========================================" -ForegroundColor Green

# Function to wait for service to be ready
function Wait-ForService {
    param([string]$Url, [string]$ServiceName, [int]$TimeoutSeconds = 90)
    
    Write-Host "[WAIT] Waiting for $ServiceName to be ready..." -ForegroundColor Yellow
    $timeout = (Get-Date).AddSeconds($TimeoutSeconds)
    $attempts = 0
    
    do {
        try {
            $attempts++
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Host "[OK] $ServiceName is ready! (attempt $attempts)" -ForegroundColor Green
                return $true
            }
        }
        catch {
            if ($attempts % 10 -eq 0) {
                Write-Host "[INFO] Still waiting for $ServiceName... (attempt $attempts)" -ForegroundColor Cyan
            }
        }
        Start-Sleep -Seconds 3
    } while ((Get-Date) -lt $timeout)
    
    Write-Host "[ERROR] $ServiceName failed to start within $TimeoutSeconds seconds" -ForegroundColor Red
    return $false
}

# Function to start service in new window
function Start-ServiceInNewWindow {
    param([string]$Path, [string]$ServiceName, [string]$Port)
    
    Write-Host "[START] Starting $ServiceName on port $Port..." -ForegroundColor Cyan
    $windowTitle = "JWT-SL: $ServiceName (Port $Port)"
    $command = "cd '$Path'; `$Host.UI.RawUI.WindowTitle='$windowTitle'; mvn spring-boot:run"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
    Write-Host "[DONE] $ServiceName launched in new window" -ForegroundColor Green
}

# Get current directory
$rootPath = Get-Location

try {
    Write-Host "[INFO] Starting services in dependency order..." -ForegroundColor White
    
    # Step 1: Start Eureka Server (Service Discovery)
    Write-Host "`n[STEP 1/6] Starting Eureka Server (Service Discovery)" -ForegroundColor Magenta
    Start-ServiceInNewWindow -Path "$rootPath\eureka-server" -ServiceName "Eureka Server" -Port "8761"
    
    # Wait for Eureka to be ready
    if (-not (Wait-ForService -Url "http://localhost:8761" -ServiceName "Eureka Server")) {
        throw "Eureka Server failed to start - cannot proceed"
    }

    # Step 2: Start Config Server (Centralized Configuration)
    Write-Host "`n[STEP 2/6] Starting Config Server (Centralized Configuration)" -ForegroundColor Magenta
    Start-ServiceInNewWindow -Path "$rootPath\config-server" -ServiceName "Config Server" -Port "8888"
    
    # Wait for Config Server to be ready
    if (-not (Wait-ForService -Url "http://localhost:8888/actuator/health" -ServiceName "Config Server")) {
        throw "Config Server failed to start - cannot proceed"
    }

    # Step 3: Start Authentication Service
    Write-Host "`n[STEP 3/6] Starting Authentication Service (JWT Management)" -ForegroundColor Magenta
    Start-ServiceInNewWindow -Path "$rootPath\authentication" -ServiceName "Authentication Service" -Port "8080"
    
    # Step 4: Start Management Service
    Write-Host "`n[STEP 4/6] Starting Management Service (Student CRUD)" -ForegroundColor Magenta
    Start-ServiceInNewWindow -Path "$rootPath\management" -ServiceName "Management Service" -Port "8081"
    
    # Step 5: Start Library Service
    Write-Host "`n[STEP 5/6] Starting Library Service (Book Management)" -ForegroundColor Magenta
    Start-ServiceInNewWindow -Path "$rootPath\library" -ServiceName "Library Service" -Port "8082"

    # Wait for services to register with Eureka
    Write-Host "`n[WAIT] Allowing time for service registration..." -ForegroundColor Yellow
    Start-Sleep -Seconds 20

    # Step 6: Start API Gateway (Entry Point)
    Write-Host "`n[STEP 6/6] Starting API Gateway (Single Entry Point)" -ForegroundColor Magenta
    Start-ServiceInNewWindow -Path "$rootPath\api-gateway" -ServiceName "API Gateway" -Port "8083"
    
    # Wait for API Gateway to be ready (check if it responds, even if health is DOWN due to dependencies)
    Write-Host "[INFO] Checking if API Gateway is responding..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8083/actuator/health" -UseBasicParsing -TimeoutSec 10 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "[OK] API Gateway is responding!" -ForegroundColor Green
        } else {
            throw "API Gateway not responding properly"
        }
    } catch {
        throw "API Gateway failed to start"
    }

    # Success message with service information
    Write-Host "`n========================================" -ForegroundColor Green
    Write-Host "   ALL SERVICES STARTED SUCCESSFULLY!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    
    Write-Host "`n[INFRASTRUCTURE SERVICES]" -ForegroundColor Cyan
    Write-Host "  Eureka Dashboard:  http://localhost:8761" -ForegroundColor White
    Write-Host "  Config Server:     http://localhost:8888/actuator/health" -ForegroundColor White
    
    Write-Host "`n[BUSINESS SERVICES - Direct Access]" -ForegroundColor Cyan
    Write-Host "  Authentication:    http://localhost:8080/actuator/health" -ForegroundColor White
    Write-Host "  Management:        http://localhost:8081/actuator/health" -ForegroundColor White
    Write-Host "  Library:           http://localhost:8082/actuator/health" -ForegroundColor White
    
    Write-Host "`n[API GATEWAY - Single Entry Point]" -ForegroundColor Yellow
    Write-Host "  Gateway Health:    http://localhost:8083/actuator/health" -ForegroundColor White
    Write-Host "  All APIs via:      http://localhost:8083" -ForegroundColor White
    
    Write-Host "`n[NEXT STEPS]" -ForegroundColor Magenta
    Write-Host "  1. Run: .\verify-phase1.ps1 to check all services" -ForegroundColor White
    Write-Host "  2. Check Eureka dashboard for service registration" -ForegroundColor White
    Write-Host "  3. Test API endpoints through the gateway" -ForegroundColor White

}
catch {
    Write-Host "`n[ERROR] Startup failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "[INFO] Check individual service windows for detailed error logs" -ForegroundColor Yellow
    Write-Host "[INFO] Run .\stop-phase1.ps1 to clean up before retrying" -ForegroundColor Yellow
}

Write-Host "`n========================================" -ForegroundColor Gray
Write-Host "Press any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")