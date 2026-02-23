
Get-ChildItem -Path "src/main/resources/assets/lumin/jello" | ForEach-Object {
    $newName = $_.Name.ToLower().Replace(" ", "_")
    if ($_.Name -cne $newName) {
        Write-Host "Renaming $($_.Name) to $newName"
        Rename-Item -Path $_.FullName -NewName $newName
    }
}
