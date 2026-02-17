# Script simple para prueba rápida de creación de franquicias
# Genera nombres aleatorios y hace requests secuenciales

param(
    [int]$Count = 50,
    [string]$BaseUrl = "http://localhost:8080"
)

Write-Host "Creando $Count franquicias..." -ForegroundColor Green
Write-Host ""

$successCount = 0
$failCount = 0
$ids = @()

for ($i = 1; $i -le $Count; $i++) {
    $randomNumber = Get-Random -Minimum 1 -Maximum 1001
    $franchiseName = "fra$randomNumber"
    
    $body = @{
        name = $franchiseName
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/franchises" `
            -Method Post `
            -ContentType "application/json" `
            -Body $body `
            -ErrorAction Stop
        
        $ids += $response.data.id
        $successCount++
        Write-Host "[$i/$Count] ✓ Creada: ID=$($response.data.id), Name=$franchiseName" -ForegroundColor Green
    }
    catch {
        $failCount++
        Write-Host "[$i/$Count] ✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    # Pequeña pausa para no saturar
    Start-Sleep -Milliseconds 50
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RESUMEN" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Exitosos: $successCount" -ForegroundColor Green
Write-Host "Fallidos: $failCount" -ForegroundColor Red
Write-Host "IDs únicos: $(($ids | Select-Object -Unique).Count)" -ForegroundColor Yellow
Write-Host "Total IDs: $($ids.Count)" -ForegroundColor Yellow

if (($ids | Select-Object -Unique).Count -eq $ids.Count) {
    Write-Host ""
    Write-Host "✓ Todos los IDs son únicos!" -ForegroundColor Green
}
else {
    Write-Host ""
    Write-Host "⚠️  Hay IDs duplicados!" -ForegroundColor Red
}
