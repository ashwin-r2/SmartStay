# StaySmart AI - Stop Local Environment
Write-Host "=== Stopping StaySmart AI Local Services ===" -ForegroundColor Cyan

# Stop Backend (Port 8080)
$port8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
if ($port8080) {
    foreach ($pid_val in $port8080) {
        Stop-Process -Id $pid_val -Force -ErrorAction SilentlyContinue
        Write-Host "[OK] Stopped backend process (PID: $pid_val)" -ForegroundColor Green
    }
} else {
    Write-Host "[INFO] No process listening on port 8080" -ForegroundColor Gray
}

# Stop Frontend (Port 5173)
$port5173 = Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
if ($port5173) {
    foreach ($pid_val in $port5173) {
        Stop-Process -Id $pid_val -Force -ErrorAction SilentlyContinue
        Write-Host "[OK] Stopped frontend process (PID: $pid_val)" -ForegroundColor Green
    }
} else {
    Write-Host "[INFO] No process listening on port 5173" -ForegroundColor Gray
}

Write-Host "=== All StaySmart AI services stopped ===" -ForegroundColor Cyan
