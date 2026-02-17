# Script de prueba de carga para creación de franquicias
# Ejecuta múltiples requests concurrentes para probar la generación de IDs

param(
    [int]$TotalRequests = 100,
    [int]$ConcurrentRequests = 10,
    [string]$BaseUrl = "http://localhost:8080"
)

Write-Host "Iniciando prueba de carga..." -ForegroundColor Green
Write-Host "Total de requests: $TotalRequests" -ForegroundColor Yellow
Write-Host "Requests concurrentes: $ConcurrentRequests" -ForegroundColor Yellow
Write-Host "URL: $BaseUrl/api/franchises" -ForegroundColor Yellow
Write-Host ""

$results = @()
$startTime = Get-Date

# Función para crear una franquicia
function CreateFranchise {
    param([int]$RequestNumber)
    
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
        
        return @{
            Success = $true
            RequestNumber = $RequestNumber
            FranchiseName = $franchiseName
            ResponseId = $response.data.id
            Code = $response.code
            Message = $response.message
            Timestamp = Get-Date
        }
    }
    catch {
        return @{
            Success = $false
            RequestNumber = $RequestNumber
            FranchiseName = $franchiseName
            Error = $_.Exception.Message
            Timestamp = Get-Date
        }
    }
}

# Crear jobs para requests concurrentes
$jobs = @()
$completed = 0
$failed = 0
$ids = @()

for ($i = 1; $i -le $TotalRequests; $i++) {
    # Si hay demasiados jobs corriendo, esperar
    while ((Get-Job -State Running).Count -ge $ConcurrentRequests) {
        Start-Sleep -Milliseconds 100
    }
    
    # Crear job
    $job = Start-Job -ScriptBlock ${function:CreateFranchise} -ArgumentList $i
    $jobs += $job
    
    Write-Progress -Activity "Enviando requests" -Status "Request $i de $TotalRequests" -PercentComplete (($i / $TotalRequests) * 100)
}

Write-Host "Esperando que completen todos los requests..." -ForegroundColor Cyan

# Recopilar resultados
foreach ($job in $jobs) {
    $result = Receive-Job -Job $job -Wait
    $results += $result
    
    if ($result.Success) {
        $completed++
        $ids += $result.ResponseId
        Write-Host "✓ Request $($result.RequestNumber): ID = $($result.ResponseId), Name = $($result.FranchiseName)" -ForegroundColor Green
    }
    else {
        $failed++
        Write-Host "✗ Request $($result.RequestNumber): Error - $($result.Error)" -ForegroundColor Red
    }
    
    Remove-Job -Job $job
}

$endTime = Get-Date
$duration = $endTime - $startTime

# Análisis de resultados
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RESULTADOS DE LA PRUEBA DE CARGA" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Total de requests: $TotalRequests" -ForegroundColor White
Write-Host "Requests exitosos: $completed" -ForegroundColor Green
Write-Host "Requests fallidos: $failed" -ForegroundColor Red
Write-Host "Tiempo total: $($duration.TotalSeconds) segundos" -ForegroundColor Yellow
Write-Host "Requests por segundo: $([math]::Round($TotalRequests / $duration.TotalSeconds, 2))" -ForegroundColor Yellow
Write-Host ""

# Verificar IDs únicos
$uniqueIds = $ids | Select-Object -Unique
$duplicateIds = $ids | Group-Object | Where-Object { $_.Count -gt 1 }

Write-Host "IDs generados: $($ids.Count)" -ForegroundColor White
Write-Host "IDs únicos: $($uniqueIds.Count)" -ForegroundColor White

if ($duplicateIds) {
    Write-Host ""
    Write-Host "⚠️  ADVERTENCIA: Se encontraron IDs duplicados!" -ForegroundColor Red
    foreach ($dup in $duplicateIds) {
        Write-Host "   ID '$($dup.Name)' aparece $($dup.Count) veces" -ForegroundColor Red
    }
}
else {
    Write-Host ""
    Write-Host "✓ Todos los IDs son únicos - La generación atómica funciona correctamente!" -ForegroundColor Green
}

# Guardar resultados en archivo CSV
$results | Export-Csv -Path "load-test-results.csv" -NoTypeInformation -Encoding UTF8
Write-Host ""
Write-Host "Resultados guardados en: load-test-results.csv" -ForegroundColor Cyan
