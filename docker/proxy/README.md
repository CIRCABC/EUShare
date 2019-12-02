
``` batch
keytool -genkey -v -alias EUShareAlias -keyalg RSA -keysize 2048 -validity 3652 -keystore EUShareKeyStore.jks -storepass password
```

``` batch
keytool -export -v -rfc -alias EUShareAlias -file EUShareCert.cer -keystore EUShareKeyStore.jks
```

``` batch
keytool -importkeystore -srckeystore EUShareKeyStore.jks -destkeystore EUShareKeyStore.p12 -deststoretype PKCS12
```

``` batch
openssl pkcs12 -in EUShareKeyStore.p12 -nokeys -out EUShareCertificate.crt
```

``` batch
openssl pkcs12 -in EUShareKeyStore.p12 -nocerts -nodes -out EUShareKey.key
``` 
