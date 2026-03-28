$baseUrl = "http://localhost:8090/api/v1"

Write-Host "1. Creating Portfolio via Go Gateway (:8090)..."
$portBody = @{
    name = "Gateway E2E Test"
    userId = "tester_8090"
} | ConvertTo-Json
$portRes = Invoke-RestMethod -Uri "$baseUrl/portfolios" -Method Post -Body $portBody -ContentType "application/json"
$portId = $portRes.id
Write-Host "Created Portfolio ID: $portId"
Write-Host "Initial Cash Balance: $($portRes.cashBalance)"

Write-Host "`n2. Depositing \$20,000 Cash..."
$cashBody = @{
    action = "DEPOSIT"
    amount = 20000
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId/cash" -Method Post -Body $cashBody -ContentType "application/json"

$portAfterCash = Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId" -Method Get
Write-Host "Cash Balance: $($portAfterCash.cashBalance)"

Write-Host "`n3. Buying 20 AAPL @ \$100..."
$buyBody = @{
    action = "BUY"
    tickerSymbol = "AAPL"
    volume = 20
    price = 100.00
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId/transactions" -Method Post -Body $buyBody -ContentType "application/json"

$portAfterBuy = Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId" -Method Get
Write-Host "Cash Balance After Buy: $($portAfterBuy.cashBalance)"
Write-Host "Portfolio Items Volume: $($portAfterBuy.items[0].volume)"

Write-Host "`n4. Selling 10 AAPL @ \$150..."
$sellBody = @{
    action = "SELL"
    tickerSymbol = "AAPL"
    volume = 10
    price = 150.00
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId/transactions" -Method Post -Body $sellBody -ContentType "application/json"

$portAfterSell = Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId" -Method Get
Write-Host "Cash Balance After Sell: $($portAfterSell.cashBalance)"
Write-Host "Portfolio Items Volume: $($portAfterSell.items[0].volume)"

Write-Host "`n5. Fetching Transaction Ledger..."
$txs = Invoke-RestMethod -Uri "$baseUrl/portfolios/$portId/transactions" -Method Get
$txs | ForEach-Object {
    Write-Host "Tx ID: $($_.id) | Action: $($_.transactionType) | Ticker: $($_.tickerSymbol) | Volume: $($_.volume) | Price: $($_.price)"
}

Write-Host "`nAll operations succeeded exclusively through Go Gateway (8090)!"
