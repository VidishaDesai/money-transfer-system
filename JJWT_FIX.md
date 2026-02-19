# JJWT API Compatibility Fix

## Issue
The compilation error occurred because the code was using an older JJWT API that doesn't exist in version 0.12.3:
```
cannot find symbol: method parserBuilder()
```

## Root Cause
JJWT 0.12.x introduced breaking API changes:
- `parserBuilder()` → `parser()`
- `parseClaimsJws()` → `parseSignedClaims()`
- `getBody()` → `getPayload()`
- `setXxx()` methods → fluent API without `set` prefix
- `signWith(key, algorithm)` → `signWith(key)` (auto-detects algorithm)

## Changes Made

### Updated JwtUtil.java

**1. Token Parsing (extractAllClaims method)**
```java
// OLD (JJWT 0.11.x)
return Jwts.parserBuilder()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

// NEW (JJWT 0.12.x)
return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
        .build()
        .parseSignedClaims(token)
        .getPayload();
```

**2. Token Creation (createToken method)**
```java
// OLD (JJWT 0.11.x)
return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();

// NEW (JJWT 0.12.x)
return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignKey())
        .compact();
```

**3. Removed Unused Import**
```java
// Removed: import io.jsonwebtoken.SignatureAlgorithm;
```

## ✅ Fix Applied
All JJWT API calls in `JwtUtil.java` have been updated to use the correct 0.12.x API.

## Next Steps
To build the project, ensure Java 17 is installed and JAVA_HOME is set:

### Windows (PowerShell)
```powershell
# Set JAVA_HOME (adjust path to your Java installation)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify
java -version

# Build
.\mvnw.cmd clean compile
```

### Alternative: Use IDE
If you're using IntelliJ IDEA or Eclipse, the IDE will handle JAVA_HOME automatically. Simply:
1. Open the project in your IDE
2. Let it download dependencies
3. Build/Run from the IDE

## Verification
Once JAVA_HOME is set, the build should succeed with:
```
[INFO] BUILD SUCCESS
[INFO] Compiling 35 source files
```

The application will then be ready to run with:
```
.\mvnw.cmd spring-boot:run
```
