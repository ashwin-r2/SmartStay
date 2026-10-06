# StaySmart AI - Start Local Environment
Write-Host "=== Starting StaySmart AI Locally ===" -ForegroundColor Cyan

# 1. Load root .env
if (Test-Path "$PSScriptRoot\.env") {
    Get-Content "$PSScriptRoot\.env" | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
            $parts = $line.Split('=', 2)
            $name = $parts[0].Trim()
            $val = $parts[1].Trim()
            [System.Environment]::SetEnvironmentVariable($name, $val, [System.EnvironmentVariableTarget]::Process)
        }
    }
    Write-Host "[OK] Loaded environment variables from .env" -ForegroundColor Green
} else {
    Write-Host "[WARN] .env not found in project root" -ForegroundColor Yellow
}

# 2. Check Backend (Port 8080)
$backendConn = Test-NetConnection -ComputerName localhost -Port 8080 -WarningAction SilentlyContinue
if ($backendConn.TcpTestSucceeded) {
    Write-Host "[OK] Backend is already running on http://localhost:8080/api" -ForegroundColor Green
} else {
    $javaBin = if (Test-Path "C:\Users\ashwi\.jdks\ms-21.0.7\bin\java.exe") { "C:\Users\ashwi\.jdks\ms-21.0.7\bin\java.exe" } else { "java" }
    Start-Process -FilePath $javaBin -ArgumentList "-jar", "target/staysmart-ai-backend.jar" -WorkingDirectory "$PSScriptRoot\backend" -WindowStyle Hidden
    Start-Sleep -Seconds 6
}

# 3. Check Frontend (Port 5173)
$frontendConn = Test-NetConnection -ComputerName localhost -Port 5173 -WarningAction SilentlyContinue
if ($frontendConn.TcpTestSucceeded) {
    Write-Host "[OK] Frontend is already running on http://localhost:5173" -ForegroundColor Green
} else {
    Write-Host "[...] Starting Vite frontend on http://localhost:5173 ..." -ForegroundColor Yellow
    Start-Process -FilePath "npm.cmd" -ArgumentList "run", "dev" -WorkingDirectory "$PSScriptRoot\frontend" -WindowStyle Hidden
    Start-Sleep -Seconds 3
}

Write-Host "`n=== Application Ready ===" -ForegroundColor Cyan
Write-Host "Frontend:   http://localhost:5173" -ForegroundColor Green
Write-Host "Backend:    http://localhost:8080/api" -ForegroundColor Green
Write-Host "Swagger UI: http://localhost:8080/api/swagger-ui.html" -ForegroundColor Green
