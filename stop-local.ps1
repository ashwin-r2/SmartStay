# StaySmart AI - Stop Local Environment
Write-Host "=== Stopping StaySmart AI Local Services ===" -ForegroundColor Cyan

# Stops whatever is listening on $Port. Only LISTEN sockets are considered: lingering
# TIME_WAIT connections report PID 0 (System Idle), which must never be targeted.
function Stop-PortListener([int]$Port, [string]$Name) {
    $pids = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -ExpandProperty OwningProcess -Unique |
        Where-Object { $_ -gt 4 }
    if (-not $pids) {
        Write-Host "[INFO] No process listening on port $Port" -ForegroundColor Gray
        return
    }
    foreach ($pid_val in $pids) {
        try {
            Stop-Process -Id $pid_val -Force -ErrorAction Stop
            Write-Host "[OK] Stopped $Name process (PID: $pid_val)" -ForegroundColor Green
        } catch {
            Write-Host "[WARN] Could not stop $Name process (PID: $pid_val): $($_.Exception.Message)" -ForegroundColor Yellow
        }
    }
}

Stop-PortListener -Port 8080 -Name "backend"
Stop-PortListener -Port 5173 -Name "frontend"

Write-Host "=== All StaySmart AI services stopped ===" -ForegroundColor Cyan
