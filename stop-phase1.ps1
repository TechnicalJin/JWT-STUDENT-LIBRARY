# JWT-STUDENT-LIBRARY Phase 1 Stop Script
# Stops all microservices running on the configured ports
# Authentication: 8080 | Management: 8081 | Library: 8082 | API Gateway: 8083

Write-Host "=======================================" -ForegroundColor Red
Write-Host "   JWT-STUDENT-LIBRARY Phase 1 Stop" -ForegroundColor Red
Write-Host "=======================================" -ForegroundColor Red

# Function to kill process by port
function Stop-ServiceByPort {
    param([int]$Port, [string]$ServiceName)
    
    try {
        Write-Host "[STOP] Checking for $ServiceName on port $Port..." -ForegroundColor Yellow
        
        # Find process using the port
        $processInfo = netstat -ano | Select-String ":$Port\s" | Select-String "LISTENING"
        
        if ($processInfo) {
            # Extract PID from netstat output
            $processInfo = $processInfo -replace '\s+', ' '
            $parts = $processInfo.ToString().Split(' ')
            $pid = $parts[-1]
            
            if ($pid -and $pid -match '^\d+$') {
                $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
                if ($process) {
                    Write-Host "[KILL] Stopping $ServiceName (PID: $pid)" -ForegroundColor Red
                    Stop-Process -Id $pid -Force
                    Write-Host "[OK] $ServiceName stopped successfully" -ForegroundColor Green
                } else {
                    Write-Host "[INFO] Process with PID $pid not found" -ForegroundColor Gray
                }
            }
        } else {
            Write-Host "[INFO] No process found on port $Port" -ForegroundColor Gray
        }
    }
    catch {
        Write-Host "[ERROR] Failed to stop $ServiceName`: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Function to stop all Java processes with specific titles
function Stop-JavaProcessesByTitle {
    Write-Host "`n[CLEANUP] Stopping Java processes with JWT-SL titles..." -ForegroundColor Yellow
    
    try {
        $javaProcesses = Get-WmiObject Win32_Process | Where-Object { 
            $_.CommandLine -like "*java*" -and 
            $_.CommandLine -like "*spring-boot*" 
        }
        
        foreach ($process in $javaProcesses) {
            try {
                $proc = Get-Process -Id $process.ProcessId -ErrorAction SilentlyContinue
                if ($proc -and $proc.MainWindowTitle -like "*JWT-SL*") {
                    Write-Host "[KILL] Stopping Java process: $($proc.MainWindowTitle) (PID: $($process.ProcessId))" -ForegroundColor Red
                    Stop-Process -Id $process.ProcessId -Force
                }
            }
            catch {
                # Process might already be stopped
            }
        }
    }
    catch {
        Write-Host "[WARNING] Error during Java process cleanup: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

try {
    Write-Host "[INFO] Stopping all Phase 1 services..." -ForegroundColor White
    
    # Stop services in reverse order (opposite of startup)
    Write-Host "`n[STEP 1/6] Stopping API Gateway..." -ForegroundColor Magenta
    Stop-ServiceByPort -Port 8083 -ServiceName "API Gateway"
    
    Write-Host "`n[STEP 2/6] Stopping Library Service..." -ForegroundColor Magenta
    Stop-ServiceByPort -Port 8082 -ServiceName "Library Service"
    
    Write-Host "`n[STEP 3/6] Stopping Management Service..." -ForegroundColor Magenta
    Stop-ServiceByPort -Port 8081 -ServiceName "Management Service"
    
    Write-Host "`n[STEP 4/6] Stopping Authentication Service..." -ForegroundColor Magenta
    Stop-ServiceByPort -Port 8080 -ServiceName "Authentication Service"
    
    Write-Host "`n[STEP 5/6] Stopping Config Server..." -ForegroundColor Magenta
    Stop-ServiceByPort -Port 8888 -ServiceName "Config Server"
    
    Write-Host "`n[STEP 6/6] Stopping Eureka Server..." -ForegroundColor Magenta
    Stop-ServiceByPort -Port 8761 -ServiceName "Eureka Server"
    
    # Additional cleanup for any remaining Java processes
    Stop-JavaProcessesByTitle
    
    # Wait a moment for processes to fully terminate
    Write-Host "`n[WAIT] Allowing time for process cleanup..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    
    # Verify all services are stopped
    Write-Host "`n[VERIFY] Checking if all services are stopped..." -ForegroundColor Cyan
    $ports = @(8080, 8081, 8082, 8083, 8761, 8888)
    $stillRunning = @()
    
    foreach ($port in $ports) {
        $processInfo = netstat -ano | Select-String ":$port\s" | Select-String "LISTENING"
        if ($processInfo) {
            $stillRunning += $port
        }
    }
    
    if ($stillRunning.Count -eq 0) {
        Write-Host "`n=======================================" -ForegroundColor Green
        Write-Host "   ALL SERVICES STOPPED SUCCESSFULLY!" -ForegroundColor Green
        Write-Host "=======================================" -ForegroundColor Green
        Write-Host "[INFO] All ports are now free:" -ForegroundColor White
        Write-Host "  Port 8080: Authentication Service - STOPPED" -ForegroundColor Gray
        Write-Host "  Port 8081: Management Service     - STOPPED" -ForegroundColor Gray
        Write-Host "  Port 8082: Library Service        - STOPPED" -ForegroundColor Gray
        Write-Host "  Port 8083: API Gateway            - STOPPED" -ForegroundColor Gray
        Write-Host "  Port 8761: Eureka Server          - STOPPED" -ForegroundColor Gray
        Write-Host "  Port 8888: Config Server          - STOPPED" -ForegroundColor Gray
    } else {
        Write-Host "`n[WARNING] Some services may still be running:" -ForegroundColor Yellow
        foreach ($port in $stillRunning) {
            Write-Host "  Port $port is still in use" -ForegroundColor Red
        }
        Write-Host "[INFO] You may need to manually kill these processes" -ForegroundColor Yellow
    }
    
}
catch {
    Write-Host "`n[ERROR] Error during shutdown: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "[INFO] Some services may still be running - check Task Manager" -ForegroundColor Yellow
}

Write-Host "`n[CLEANUP COMPLETE] Ready for fresh startup" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Gray
Write-Host "Press any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")