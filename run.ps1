# Requires: Java 11+ installed and available in PATH
# If Maven is available, this script will use it to build and run.
# Otherwise, it falls back to manual javac + java with the Gson jar.

param(
    [int]$Port = 8081
)

# Move to the script directory
Set-Location -LiteralPath "$PSScriptRoot"

function Has-Command($name) {
    $null -ne (Get-Command $name -ErrorAction SilentlyContinue)
}

# Prefer Maven if available
if (Has-Command mvn -or Has-Command mvn.cmd -or Has-Command mvnw) {
    Write-Host "Using Maven to build and run..."
    # Ensure Java version is present (mvn will also fail early otherwise)
    $javaVersion = (& java -version) 2>&1 | Select-Object -First 1
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Java is not installed or not in PATH. Install Java 11+ and retry."
        exit 1
    }
    Write-Host "Detected $javaVersion"

    # Clean, compile, and run main class
    mvn -q -DskipTests clean compile exec:java -Dexec.mainClass=ApiServer
    exit $LASTEXITCODE
}

Write-Host "Maven not found; falling back to manual javac/java..."

# Manual path
$javaVersion = (& java -version) 2>&1 | Select-Object -First 1
if ($LASTEXITCODE -ne 0) {
    Write-Error "Java is not installed or not in PATH. Install Java 11+ and retry."
    exit 1
}
Write-Host "Detected $javaVersion"

# Prepare output directory
if (Test-Path out) { Remove-Item -Recurse -Force out }
New-Item -ItemType Directory -Path out | Out-Null

# Compile
$classpath = ".;lib\gson-2.13.2.jar"
javac -encoding UTF-8 -cp $classpath -d out src\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed. Check errors above."
    exit 1
}

# Run
$runClasspath = ".;out;lib\gson-2.13.2.jar"
Write-Host "Starting ApiServer on port $Port ..."
java -cp $runClasspath ApiServer
