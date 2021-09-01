# Nginx proxy docker image
## Configurations
### With no SSL endpoint
./noSSL_nginx.conf
Redirects the API calls to the backend using /webservice/ as a path to the backend
Redirects all the other calls to the front-end

### With SSL endpoint
./conf/nginx.conf
Does the same as no SSL enpoint but also adds the SSL endpoint using a certificate and a certificate key.

./conf/certificate.crt & ./conf/certificateKey.key 
Can be generated using the following commands:
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
