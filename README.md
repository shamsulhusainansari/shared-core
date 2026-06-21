# Shared Core Utilities

[![Maven Central](https://img.shields.io/badge/maven--central-v1.0.1-blue.svg)](https://github.com/shamsulhusainansari/shared-core)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-Proprietary-red.svg)](LICENSE)

A comprehensive Java library providing reusable encryption utilities and logging masking capabilities for enterprise applications.

## 🚀 Features

### 🔐 Encryption Utilities
- **AES-256 Encryption**: GCM and CBC modes with automatic IV generation
- **RSA Encryption**: 2048/4096-bit key support with PKCS1 and OAEP padding
- **Hash Functions**: SHA-256 hashing in Base64 and Hexadecimal formats
- **Digital Signatures**: RSA signature generation and verification

### 🛡️ Logging Masking
- **Automatic Masking**: Masks 14+ types of sensitive data in logs
- **Runtime Configuration**: Enable/disable via application properties
- **Extensible**: Add custom masking patterns easily
- **Performance Optimized**: Minimal overhead with async appenders

## 📋 Requirements

- Java 17 or higher
- Maven 3.6 or higher
- Spring Boot 3.x (optional, for logging configuration)

## 📦 Installation

### Maven

```xml
<dependency>
    <groupId>com.shared.core</groupId>
    <artifactId>shared-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.shared.core:shared-core:1.0.1'
```

## 🔧 Quick Start

### 1. AES Encryption

```java
import com.company.shared.utils.AESEncryptionUtil;
import javax.crypto.SecretKey;

// Generate key
SecretKey key = AESEncryptionUtil.generateAES256Key();

// Encrypt
String encrypted = AESEncryptionUtil.encryptAES256GCM("Sensitive data", key);

// Decrypt
String decrypted = AESEncryptionUtil.decryptAES256GCM(encrypted, key);
```

### 2. RSA Encryption

```java
import com.company.shared.utils.RSAEncryptionUtil;
import java.security.KeyPair;

// Generate key pair
KeyPair keyPair = RSAEncryptionUtil.generateRSAKeyPair();

// Encrypt
String encrypted = RSAEncryptionUtil.encrypt("Secret message", keyPair.getPublic());

// Decrypt
String decrypted = RSAEncryptionUtil.decrypt(encrypted, keyPair.getPrivate());
```

### 3. Logging Masking

#### Step 1: Configure in Main Class

```java
import com.company.shared.logging.MaskingPatternLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class Application {
    
    @Value("${logging.redaction:true}")
    protected Boolean redaction;
    
    @PostConstruct
    public void init() {
        MaskingPatternLayout.setMaskingEnabled(redaction);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### Step 2: Create logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="com.company.shared.logging.MaskingPatternLayout">
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </layout>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

#### Step 3: Configure in application.properties

```properties
# Enable/disable masking
logging.redaction=true
```

#### Step 4: Use Normal Logging

```java
logger.info("User email: john@example.com, mobile: 9876543210");
// Output: User email: XXXXXXXe@example.com, mobile: XXXXXX3210
```

## 📚 Documentation

- **[Deployment Guide](DEPLOYMENT_GUIDE.md)** - Build, test, and deploy instructions
- **[Logging Masking Guide](LOGGING_MASKING_GUIDE.md)** - Complete masking documentation
- **[Quick Reference](QUICK_REFERENCE.md)** - Encryption utilities quick reference
- **[Integration Examples](INTEGRATION_EXAMPLE.md)** - Complete Spring Boot integration examples

## 🔒 Masked Data Types

The logging masking feature automatically masks:

| Type | Example | Masked Output |
|------|---------|---------------|
| Email | john@example.com | XXXXXXXe@example.com |
| Mobile | 9876543210 | XXXXXX3210 |
| Aadhaar | 123456789012 | XXXXXXXX9012 |
| PAN Card | ABCDE1234F | XXXXXXX34F |
| Credit Card | 4532-1234-5678-9010 | XXXX-XXXX-XXXX-9010 |
| Password | password=secret | XXXXXXXXXX |
| API Key | apikey=sk_test_123 | XXXXXXXXXX |
| SSN | 123-45-6789 | XXX-XX-6789 |

...and 6 more types!

## 🎯 Use Cases

### Secure Data Storage
```java
// Encrypt sensitive data before storing in database
SecretKey key = AESEncryptionUtil.getKeyFromString(masterPassword);
String encrypted = AESEncryptionUtil.encryptAES256GCM(sensitiveData, key);
// Store encrypted in database
```

### Secure Communication
```java
// Encrypt message with recipient's public key
String encrypted = RSAEncryptionUtil.encrypt(message, recipientPublicKey);
String signature = RSAEncryptionUtil.sign(message, senderPrivateKey);
// Send encrypted message and signature
```

### Password Hashing
```java
// Hash password before storage
String passwordHash = AESEncryptionUtil.generateSHA256HashHex(password);
// Store hash in database
```

## ⚙️ Configuration Options

### Masking Configuration

```properties
# Enable/disable masking (default: true)
logging.redaction=true

# Alternative property names
logging.masking.enable=true
```

### System Property
```bash
java -Dlogging.redaction=true -jar your-app.jar
```

### Environment Variable
```bash
export LOGGING_REDACTION=true
```

### Programmatic Control
```java
MaskingPatternLayout.setMaskingEnabled(true);
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=AESEncryptionUtilTest

# Run with coverage
mvn clean test jacoco:report
```

## 🏗️ Building from Source

```bash
# Clone repository
git clone https://github.com/shamsulhusainansari/shared-core.git
cd shared-core

# Build
mvn clean install

# Package
mvn clean package

# Deploy
mvn clean deploy
```

## 📊 Project Structure

```
shared-core/
├── src/main/java/com/company/shared/
│   ├── utils/
│   │   ├── AESEncryptionUtil.java
│   │   └── RSAEncryptionUtil.java
│   └── logging/
│       ├── MaskingPatternLayout.java
│       └── LoggingMaskingConfig.java
├── src/main/resources/
│   ├── logback-spring.xml
│   └── application.properties.example
├── src/test/java/
│   └── [53 comprehensive test cases]
├── pom.xml
├── README.md
├── DEPLOYMENT_GUIDE.md
├── LOGGING_MASKING_GUIDE.md
├── QUICK_REFERENCE.md
└── INTEGRATION_EXAMPLE.md
```

## 🔐 Security Best Practices

### ✅ DO
- Use AES-256-GCM for authenticated encryption
- Use RSA-OAEP for better security
- Generate new keys for each encryption session when possible
- Store keys securely (HSM, key vault, encrypted storage)
- Enable masking in production environments
- Use at least 2048-bit RSA keys

### ❌ DON'T
- Don't hardcode encryption keys in source code
- Don't reuse IVs with the same key
- Don't use weak passwords for key derivation
- Don't store private keys in plain text
- Don't disable masking in production
- Don't log raw passwords

## 🤝 Contributing

This is an internal library. For contributions:
1. Create a feature branch
2. Make your changes
3. Add tests
4. Update documentation
5. Submit a pull request

## 📝 License

Proprietary - Internal use only

## 👥 Authors

- Shared Core Team

## 📞 Support

For issues or questions:
- Create an issue in the repository
- Contact the Shared Core Team
- Refer to the documentation guides

## 🔄 Version History

### v1.0.1 (Current)
- ✅ AES-256 encryption (GCM & CBC)
- ✅ RSA encryption (PKCS1 & OAEP)
- ✅ SHA-256 hashing
- ✅ Logging masking with runtime configuration
- ✅ Spring Boot integration
- ✅ 53 comprehensive test cases
- ✅ Complete documentation

### v1.0.0
- Initial release

## 🎓 Examples

See [INTEGRATION_EXAMPLE.md](INTEGRATION_EXAMPLE.md) for complete examples including:
- Spring Boot application setup
- REST API with automatic masking
- Service layer with encryption
- Testing examples
- Docker deployment
- Environment-specific configurations

## 🚀 Getting Started

1. **Add dependency** to your pom.xml
2. **Configure logging** with logback-spring.xml
3. **Enable masking** in your main class
4. **Use encryption utilities** in your services
5. **Test** your integration

For detailed instructions, see [INTEGRATION_EXAMPLE.md](INTEGRATION_EXAMPLE.md)

---

**Made with ❤️ by the Shared Core Team**