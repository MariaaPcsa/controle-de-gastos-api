# Create Docker secrets (requires Docker Swarm)
# Usage: Open PowerShell and run: .\scripts\create-secrets.ps1
$ErrorActionPreference = 'Stop'

$secrets = @{
    jwt_secret = 'replace_with_real_jwt_secret_here'
    postgres_password_user = 'replace_postgres_user_pw'
    postgres_password_tx = 'replace_postgres_tx_pw'
    postgres_password_analytics = 'replace_postgres_analytics_pw'
}

Write-Host "Initializing Docker Swarm (if not already):"
try {
    docker swarm init | Out-Null
} catch {
    Write-Host "Swarm init may already be configured; continuing..."
}

foreach ($name in $secrets.Keys) {
    $value = $secrets[$name]
    Write-Host "Creating secret: $name"
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($value)
    $tmp = [System.IO.Path]::GetTempFileName()
    [System.IO.File]::WriteAllBytes($tmp,$bytes)
    try {
        docker secret create $name $tmp | Out-Null
    } catch {
        Write-Host "Secret $name may already exist; skipping"
    } finally {
        Remove-Item $tmp -Force
    }
}

Write-Host "Secrets created (or already existed). Deploy with: docker stack deploy -c docker-compose.yml my_stack"
