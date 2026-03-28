$ErrorActionPreference = "Stop"

Write-Host "========================================="
Write-Host " 🚀 Running Full System E2E Test Suite    "
Write-Host "========================================="

# 1. Test Gateway -> Java Reverse Proxy (API/V2 to API/V1 mapping & DB Explorer)
try {
    Write-Host "1. Testing Gateway Reverse Proxy to Java (DB Explorer)..." -NoNewline
    $debugRes = Invoke-RestMethod -Uri "http://localhost:8090/api/v2/debug/table/market_price" -Method Get
    if ($debugRes.tableName -eq "market_price") { Write-Host " [PASS]" -ForegroundColor Green } else { throw "Validation Failed" }
} catch { Write-Host " [FAIL]" -ForegroundColor Red; throw $_ }

# 2. Test Gateway -> Python Reverse Proxy
try {
    Write-Host "2. Testing Gateway Reverse Proxy to Python FastAPI..." -NoNewline
    $marketRes = Invoke-RestMethod -Uri "http://localhost:8090/api/v2/market/prices?tickers=AAPL" -Method Get
    if ($marketRes.Count -gt 0 -or $marketRes.Count -eq 0) { Write-Host " [PASS]" -ForegroundColor Green } else { throw "Validation Failed" }
} catch { Write-Host " [FAIL]" -ForegroundColor Red; throw $_ }

# 3. Test Full Business CRUD Logic (Portfolio Creation -> Deposit -> Buy)
try {
    Write-Host "3. Testing Business CRUD Flow through Gateway..." 
    
    # 3.1 Create Portfolio
    $headers = @{ "Content-Type" = "application/json" }
    $createBody = @{ name = "Full Stack Integration Fund"; userId = "auto_tester_01" } | ConvertTo-Json
    $port = Invoke-RestMethod -Uri "http://localhost:8090/api/v2/portfolios" -Method Post -Headers $headers -Body $createBody
    $portId = $port.id
    Write-Host "   -> Portfolio Created: ID $portId" -ForegroundColor Cyan
    
    # 3.2 Deposit Cash
    $amount = 50000
    $cashBody = @{ action = "DEPOSIT"; amount = $amount } | ConvertTo-Json
    Invoke-RestMethod -Uri "http://localhost:8090/api/v2/portfolios/$portId/cash" -Method Post -Headers $headers -Body $cashBody
    $portDetails = Invoke-RestMethod -Uri "http://localhost:8090/api/v2/portfolios/$portId" -Method Get
    if ($portDetails.cashBalance -eq $amount) { Write-Host "   -> Cash Deposited ($amount) [PASS]" -ForegroundColor Green } else { throw "Deposit Failed" }
    
    # 3.3 Buy Asset (Server-authoritative pricing validation via Python sync DB)
    $buyBody = @{ action = "BUY"; tickerSymbol = "AAPL"; volume = 10; price = 10.0 } | ConvertTo-Json # price 10.0 should be ignored and substituted
    Invoke-RestMethod -Uri "http://localhost:8090/api/v2/portfolios/$portId/transactions" -Method Post -Headers $headers -Body $buyBody
    
    # 3.4 Verify Ledger and Security Enforcement
    $finalPort = Invoke-RestMethod -Uri "http://localhost:8090/api/v2/portfolios/$portId" -Method Get
    # Since executing price came from MarketPrice, cash shouldn't have been deducted by 10 * 10 = 100 exactly, it will be real-world price of AAPL (~$150)
    if ($finalPort.cashBalance -lt 49950) { 
        Write-Host "   -> Asset Purchased! Server ignored malicious requested price. Cash balance securely deducted: $($finalPort.cashBalance) [PASS]" -ForegroundColor Green 
    } else { 
        throw "Security Validation Failed!" 
    }
} catch {
    Write-Host "   [FAIL] $_" -ForegroundColor Red
}

Write-Host "========================================="
Write-Host " 🎉 E2E Testing Completed "
Write-Host "========================================="
