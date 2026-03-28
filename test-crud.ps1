$baseUrl = "http://localhost:8080/api/v1"

Write-Host "1. Creating Portfolio..."
$portBody = @{
    name = "Direct Backend Test"
    userId = "admin123"
} | ConvertTo-Json
$portRes = Invoke-RestMethod -Uri "$baseUrl/portfolios" -Method Post -Body $portBody -ContentType "application/json"
$portId = $portRes.id
Write-Host "Created Portfolio ID: $portId"
Write-Host "Cash Balance: $($portRes.cashBalance)"

Write-Host "`n2. Depositing \$10,000 Cash..."
$cashBody = @{
    action = "DEPOSIT"
    amount = 10000
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId/cash" -Method Post -Body $cashBody -ContentType "application/json"

$portAfterCash = Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId" -Method Get
Write-Host "Cash Balance: $($portAfterCash.cashBalance)"

Write-Host "`n3. Buying 10 AAPL @ \$150..."
$buyBody = @{
    action = "BUY"
    tickerSymbol = "AAPL"
    volume = 10
    price = 150.00
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId/transactions" -Method Post -Body $buyBody -ContentType "application/json"

$portAfterBuy = Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId" -Method Get
Write-Host "Cash Balance After Buy: $($portAfterBuy.cashBalance)"
Write-Host "Portfolio Items: $( ($portAfterBuy.items | ConvertTo-Json -Compress) )"

Write-Host "`n4. Selling 5 AAPL @ \$200..."
$sellBody = @{
    action = "SELL"
    tickerSymbol = "AAPL"
    volume = 5
    price = 200.00
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId/transactions" -Method Post -Body $sellBody -ContentType "application/json"

$portAfterSell = Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId" -Method Get
Write-Host "Cash Balance After Sell: $($portAfterSell.cashBalance)"
Write-Host "Portfolio Items: $( ($portAfterSell.items | ConvertTo-Json -Compress) )"

Write-Host "`n5. Getting Transaction Ledger..."
$txs = Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId/transactions" -Method Get
$txs | ForEach-Object {
    Write-Host "Tx ID: $($_.id) | Action: $($_.transactionType) | Ticker: $($_.tickerSymbol) | Volume: $($_.volume) | Price: $($_.price)"
}

Write-Host "`nAll Basic Operations Succeeded on :8080"
