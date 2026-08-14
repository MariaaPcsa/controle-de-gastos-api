# Build and bring up the whole stack for local development
# Usage: Open PowerShell in the repo root and run: .\scripts\up.ps1

$ErrorActionPreference = 'Stop'

# Copy .env.example -> .env if not exists
if (-Not (Test-Path -Path .env)) {
    Write-Host "Creating .env from .env.example"
    Copy-Item .env.example .env -Force
    Write-Host "Please edit .env to set JWT_SECRET and any passwords before proceeding if you want different values."
}

# Build all modules (use mvnw if present)
if (Test-Path ./mvnw) {
    Write-Host "Building project with ./mvnw"
    ./mvnw -DskipTests package
} else {
    Write-Host "Building project with mvn"
    mvn -DskipTests package
}

# Ensure old stack removed
Write-Host "Stopping existing containers and removing volumes"
docker-compose down --volumes --remove-orphans

# Start compose
Write-Host "Starting docker-compose (build images)"
docker-compose up --build -d

# Wait for essential services
$healthEndpoints = @(
    'http://localhost:8001/actuator/health',
    'http://localhost:8002/actuator/health',
    'http://localhost:8003/actuator/health',
    'http://localhost:8080/actuator/health'
)

$timeout = 180 # seconds
$interval = 5
$start = Get-Date

foreach ($url in $healthEndpoints) {
    Write-Host "Checking $url"
    $ok = $false
    while (-not $ok) {
        try {
            $resp = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 5
            Write-Host "OK: $url ->" ($resp.status -as [string])
            $ok = $true
        } catch {
            $elapsed = (Get-Date) - $start
            if ($elapsed.TotalSeconds -gt $timeout) {
                Write-Error "Timeout waiting for $url"
                Break
            }
            Write-Host "Waiting for $url ..."
            Start-Sleep -Seconds $interval
        }
    }
}

Write-Host "Done. Use 'docker-compose logs -f' to tail logs, or 'docker ps' to see running containers."
