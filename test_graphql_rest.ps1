#!/usr/bin/env pwsh

Write-Host "==========================================" -ForegroundColor Magenta
Write-Host "TESTS GraphQL vs REST" -ForegroundColor Magenta
Write-Host "==========================================" -ForegroundColor Magenta

# Test 1: GraphQL Query 1 - Éviter Over-Fetching
Write-Host "`n[1/4] GraphQL Query 1: Éviter Over-Fetching" -ForegroundColor Cyan
Write-Host "Requête: livres(genre:ROMAN, disponible:true) { id titre disponible }" -ForegroundColor DarkGray

try {
    $query1 = @{query = "{ livres(genre:ROMAN, disponible:true) { id titre disponible } }"} | ConvertTo-Json
    $response1 = Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -ContentType "application/json" -Body $query1 -UseBasicParsing
    $content1 = $response1.Content
    $size1 = $content1.Length
    
    Write-Host "Réponse:" -ForegroundColor Green
    $content1 | ConvertFrom-Json | ConvertTo-Json -Depth 10
    Write-Host "Taille: $size1 bytes" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "Erreur: $_" -ForegroundColor Red
}

# Test 2: GraphQL Query 2 - Éviter Under-Fetching
Write-Host "`n[2/4] GraphQL Query 2: Éviter Under-Fetching" -ForegroundColor Cyan
Write-Host "Requête: livres { titre auteur { nom nationalite } empruntsActifs { dateRetourPrevue adherent { nom } } }" -ForegroundColor DarkGray

try {
    $query2 = @{query = '{ livres { titre auteur { nom nationalite } empruntsActifs { dateRetourPrevue adherent { nom } } } }'} | ConvertTo-Json
    $response2 = Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -ContentType "application/json" -Body $query2 -UseBasicParsing
    $content2 = $response2.Content
    $size2 = $content2.Length
    
    Write-Host "Réponse:" -ForegroundColor Green
    ($content2 | ConvertFrom-Json | ConvertTo-Json -Depth 10) -split "`n" | Select-Object -First 30
    Write-Host "..." -ForegroundColor DarkGray
    Write-Host "Taille: $size2 bytes" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "Erreur: $_" -ForegroundColor Red
}

# Test 3: REST API - Over-fetching
Write-Host "`n[3/4] REST API: /api/livres (Over-fetching)" -ForegroundColor Cyan
Write-Host "Récupère TOUS les champs (~40)" -ForegroundColor DarkGray

try {
    $response3 = Invoke-WebRequest -Uri "http://localhost:8081/api/livres" -Method GET -UseBasicParsing
    $content3 = $response3.Content
    $size3 = $content3.Length
    
    Write-Host "Réponse (premiers 800 chars):" -ForegroundColor Green
    $content3.Substring(0, [Math]::Min(800, $content3.Length))
    Write-Host "`n..." -ForegroundColor DarkGray
    Write-Host "Taille TOTALE: $size3 bytes" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "Erreur: $_" -ForegroundColor Red
}

# Test 4: GraphQL Mutation
Write-Host "`n[4/4] GraphQL Mutation: Emprunter un livre" -ForegroundColor Cyan
Write-Host "Mutation: emprunterLivre(livreId: 2, adherentId: 1) { id dateRetourPrevue livre { titre } }" -ForegroundColor DarkGray

try {
    $mutation = @{query = 'mutation { emprunterLivre(livreId: "2", adherentId: "1") { id dateRetourPrevue livre { titre } } }'} | ConvertTo-Json
    $response4 = Invoke-WebRequest -Uri "http://localhost:8080/graphql" -Method POST -ContentType "application/json" -Body $mutation -UseBasicParsing
    $content4 = $response4.Content
    $size4 = $content4.Length
    
    Write-Host "Réponse:" -ForegroundColor Green
    $content4 | ConvertFrom-Json | ConvertTo-Json -Depth 10
    Write-Host "Taille: $size4 bytes" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "Erreur: $_" -ForegroundColor Red
}

# Résumé
Write-Host "`n==========================================" -ForegroundColor Magenta
Write-Host "RÉSUMÉ TAILLES" -ForegroundColor Magenta
Write-Host "==========================================" -ForegroundColor Magenta

if ($size1 -gt 0 -and $size2 -gt 0 -and $size3 -gt 0) {
    Write-Host "GraphQL Query 1 (3 champs):     $size1 bytes" -ForegroundColor Green
    Write-Host "GraphQL Query 2 (relations):   $size2 bytes" -ForegroundColor Green
    Write-Host "REST /api/livres (tous champs): $size3 bytes" -ForegroundColor Red
    Write-Host ""
    
    $ratio = [Math]::Round($size3 / $size1, 2)
    Write-Host "Réduction GraphQL vs REST: $ratio x plus petit ($(([Math]::Round((1 - $size1/$size3) * 100, 1)))% de réduction)" -ForegroundColor Yellow
} else {
    Write-Host "Impossible de calculer les ratios - certains tests ont échoué" -ForegroundColor Yellow
}
