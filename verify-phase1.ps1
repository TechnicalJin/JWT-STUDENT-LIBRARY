# JWT-STUDENT-LIBRARY Phase 1 Verification Script
# Verifies all microservices are running and properly registered
# Authentication: 8080 | Management: 8081 | Library: 8082 | API Gateway: 8083

Write-Host "=========================================" -ForegroundColor Blue
Write-Host "   JWT-STUDENT-LIBRARY Phase 1 Verify" -ForegroundColor Blue
Write-Host "=========================================" -ForegroundColor Blue

# Function to check service health
function Test-ServiceHealth {
    param([string]$Url, [string]$ServiceName, [int]$Port)
    
    try {
        Write-Host "[CHECK] Testing $ServiceName on port $Port..." -ForegroundColor Cyan
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10 -ErrorAction Stop
        
        if ($response.StatusCode -eq 200) {
            # For API Gateway, also check if it responds (even if some dependencies are down)
            if ($ServiceName -eq "API Gateway") {
                Write-Host "[OK] $ServiceName is responding!" -ForegroundColor Green
                return $true
            } else {
                Write-Host "[OK] $ServiceName is healthy!" -ForegroundColor Green
                return $true
            }
        } else {
            Write-Host "[WARN] $ServiceName returned status: $($response.StatusCode)" -ForegroundColor Yellow
            return $false
        }
    }
    catch {
        Write-Host "[ERROR] $ServiceName is not responding: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to check port availability
function Test-PortInUse {
    param([int]$Port, [string]$ServiceName)
    
    try {
        $processInfo = netstat -ano | Select-String ":$Port\s" | Select-String "LISTENING"
        if ($processInfo) {
            Write-Host "[OK] Port $Port is in use by $ServiceName" -ForegroundColor Green
            return $true
        } else {
            Write-Host "[ERROR] Port $Port is not in use - $ServiceName not running" -ForegroundColor Red
            return $false
        }
    }
    catch {
        Write-Host "[ERROR] Cannot check port $Port for $ServiceName" -ForegroundColor Red
        return $false
    }
}

# Function to check Eureka service registration
function Test-EurekaRegistration {
    try {
        Write-Host "`n[CHECK] Verifying service registration with Eureka..." -ForegroundColor Cyan
        $eurekaUrl = "http://localhost:8761/eureka/apps"
        $response = Invoke-WebRequest -Uri $eurekaUrl -UseBasicParsing -TimeoutSec 10 -ErrorAction Stop
        
        if ($response.StatusCode -eq 200) {
            $content = $response.Content
            $services = @("AUTHENTICATION-SERVICE", "MANAGEMENT-SERVICE", "LIBRARY-SERVICE", "API-GATEWAY")
            $registeredServices = @()
            
            foreach ($service in $services) {
                if ($content -like "*$service*") {
                    $registeredServices += $service
                    Write-Host "[OK] $service is registered with Eureka" -ForegroundColor Green
                } else {
                    Write-Host "[WARN] $service is NOT registered with Eureka" -ForegroundColor Yellow
                }
            }
            
            Write-Host "[INFO] $($registeredServices.Count) of $($services.Count) services registered" -ForegroundColor White
            return $registeredServices.Count -eq $services.Count
        }
    }
    catch {
        Write-Host "[ERROR] Cannot verify Eureka registration: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to test API Gateway routing
function Test-GatewayRouting {
    try {
        Write-Host "`n[CHECK] Testing API Gateway routing..." -ForegroundColor Cyan
        
        # Test routes through gateway
        $routes = @(
            @{Path="/auth/health"; Service="Authentication"}
            @{Path="/management/health"; Service="Management"}
            @{Path="/library/health"; Service="Library"}
        )
        
        $successCount = 0
        foreach ($route in $routes) {
            try {
                $gatewayUrl = "http://localhost:8083$($route.Path)"
                $response = Invoke-WebRequest -Uri $gatewayUrl -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
                
                if ($response.StatusCode -eq 200) {
                    Write-Host "[OK] Gateway route to $($route.Service) works" -ForegroundColor Green
                    $successCount++
                } else {
                    Write-Host "[WARN] Gateway route to $($route.Service) returned: $($response.StatusCode)" -ForegroundColor Yellow
                }
            }
            catch {
                Write-Host "[ERROR] Gateway route to $($route.Service) failed" -ForegroundColor Red
            }
        }
        
        Write-Host "[INFO] $successCount of $($routes.Count) gateway routes working" -ForegroundColor White
        return $successCount -eq $routes.Count
    }
    catch {
        Write-Host "[ERROR] Cannot test gateway routing: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Main verification process
try {
    Write-Host "[INFO] Starting comprehensive health check..." -ForegroundColor White
    
    # Results tracking
    $results = @{
        "PortCheck" = @{}
        "HealthCheck" = @{}
        "EurekaRegistration" = $false
        "GatewayRouting" = $false
    }
    
    # Step 1: Check if ports are in use
    Write-Host "`n[STEP 1] Checking port availability..." -ForegroundColor Magenta
    $results.PortCheck["Eureka"] = Test-PortInUse -Port 8761 -ServiceName "Eureka Server"
    $results.PortCheck["Config"] = Test-PortInUse -Port 8888 -ServiceName "Config Server"
    $results.PortCheck["Authentication"] = Test-PortInUse -Port 8080 -ServiceName "Authentication Service"
    $results.PortCheck["Management"] = Test-PortInUse -Port 8081 -ServiceName "Management Service"
    $results.PortCheck["Library"] = Test-PortInUse -Port 8082 -ServiceName "Library Service"
    $results.PortCheck["Gateway"] = Test-PortInUse -Port 8083 -ServiceName "API Gateway"
    
    # Step 2: Check service health endpoints
    Write-Host "`n[STEP 2] Checking service health endpoints..." -ForegroundColor Magenta
    $results.HealthCheck["Eureka"] = Test-ServiceHealth -Url "http://localhost:8761" -ServiceName "Eureka Server" -Port 8761
    $results.HealthCheck["Config"] = Test-ServiceHealth -Url "http://localhost:8888/actuator/health" -ServiceName "Config Server" -Port 8888
    $results.HealthCheck["Authentication"] = Test-ServiceHealth -Url "http://localhost:8080/actuator/health" -ServiceName "Authentication Service" -Port 8080
    $results.HealthCheck["Management"] = Test-ServiceHealth -Url "http://localhost:8081/actuator/health" -ServiceName "Management Service" -Port 8081
    $results.HealthCheck["Library"] = Test-ServiceHealth -Url "http://localhost:8082/actuator/health" -ServiceName "Library Service" -Port 8082
    $results.HealthCheck["Gateway"] = Test-ServiceHealth -Url "http://localhost:8083/actuator/health" -ServiceName "API Gateway" -Port 8083
    
    # Step 3: Check Eureka service registration
    Write-Host "`n[STEP 3] Checking Eureka service registration..." -ForegroundColor Magenta
    $results.EurekaRegistration = Test-EurekaRegistration
    
    # Step 4: Test API Gateway routing
    Write-Host "`n[STEP 4] Testing API Gateway routing..." -ForegroundColor Magenta
    $results.GatewayRouting = Test-GatewayRouting
    
    # Summary Report
    Write-Host "`n=========================================" -ForegroundColor Blue
    Write-Host "          VERIFICATION SUMMARY" -ForegroundColor Blue
    Write-Host "=========================================" -ForegroundColor Blue
    
    # Port check summary
    Write-Host "`n[PORT STATUS]" -ForegroundColor Cyan
    $portPassed = 0
    foreach ($service in $results.PortCheck.Keys) {
        $status = if ($results.PortCheck[$service]) { "RUNNING"; $portPassed++ } else { "STOPPED" }
        $color = if ($results.PortCheck[$service]) { "Green" } else { "Red" }
        Write-Host "  $service`: $status" -ForegroundColor $color
    }
    
    # Health check summary
    Write-Host "`n[HEALTH STATUS]" -ForegroundColor Cyan
    $healthPassed = 0
    foreach ($service in $results.HealthCheck.Keys) {
        $status = if ($results.HealthCheck[$service]) { "HEALTHY"; $healthPassed++ } else { "UNHEALTHY" }
        $color = if ($results.HealthCheck[$service]) { "Green" } else { "Red" }
        Write-Host "  $service`: $status" -ForegroundColor $color
    }
    
    # Registration and routing status
    Write-Host "`n[INTEGRATION STATUS]" -ForegroundColor Cyan
    $registrationStatus = if ($results.EurekaRegistration) { "ALL REGISTERED" } else { "SOME MISSING" }
    $registrationColor = if ($results.EurekaRegistration) { "Green" } else { "Yellow" }
    Write-Host "  Eureka Registration: $registrationStatus" -ForegroundColor $registrationColor
    
    $routingStatus = if ($results.GatewayRouting) { "ALL WORKING" } else { "SOME FAILING" }
    $routingColor = if ($results.GatewayRouting) { "Green" } else { "Yellow" }
    Write-Host "  Gateway Routing: $routingStatus" -ForegroundColor $routingColor
    
    # Overall status
    $totalPorts = $results.PortCheck.Count
    $totalHealth = $results.HealthCheck.Count
    $overallSuccess = ($portPassed -eq $totalPorts) -and ($healthPassed -eq $totalHealth) -and $results.EurekaRegistration -and $results.GatewayRouting
    
    Write-Host "`n[OVERALL STATUS]" -ForegroundColor Magenta
    if ($overallSuccess) {
        Write-Host "  ALL SYSTEMS OPERATIONAL!" -ForegroundColor Green
        Write-Host "  Phase 1 infrastructure is fully functional" -ForegroundColor Green
    } else {
        Write-Host "  SOME ISSUES DETECTED" -ForegroundColor Yellow
        Write-Host "  Check individual service logs for details" -ForegroundColor Yellow
    }
    
    # Access URLs
    Write-Host "`n[ACCESS URLS]" -ForegroundColor Cyan
    Write-Host "  Eureka Dashboard:  http://localhost:8761" -ForegroundColor White
    Write-Host "  Config Server:     http://localhost:8888/actuator/health" -ForegroundColor White
    Write-Host "  API Gateway:       http://localhost:8083" -ForegroundColor Yellow
    Write-Host "  Direct Services:" -ForegroundColor White
    Write-Host "    Authentication:  http://localhost:8080" -ForegroundColor Gray
    Write-Host "    Management:      http://localhost:8081" -ForegroundColor Gray
    Write-Host "    Library:         http://localhost:8082" -ForegroundColor Gray
    
}
catch {
    Write-Host "`n[ERROR] Verification failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "[INFO] Some services may not be running properly" -ForegroundColor Yellow
}

Write-Host "`n=========================================" -ForegroundColor Gray
Write-Host "Press any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")