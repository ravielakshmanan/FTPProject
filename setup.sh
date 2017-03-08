#!/bin/bash
# setup script for Group6

echo "setting up the environment...."

# create the directories

#echo "creating client and server directories...."

#mkdir client
#mkdir server

echo "use keytool utility to generate keystores, key pairs  and certificates...."

keytool -genkeypair -alias client -keyalg RSA -dname "CN=client, OU=4180Group6, O=Columbia University, L=New York City, S=New York, C=US" -keypass client -storepass client -validity 365 -keystore client.keystore -storetype jks

keytool -genkeypair -alias server -keyalg RSA -dname "CN=server, OU=4180Group6, O=Columbia University, L=New York City, S=New York, C=US" -keypass server -storepass server -validity 365 -keystore server.keystore -storetype jks

echo "export certificates and import them into counterpart's trustsstore"

keytool -exportcert -alias client -file client.pfx -storetype jks -keystore client.keystore -storepass client

keytool -exportcert -alias server -file server.pfx -storetype jks -keystore server.keystore -storepass server

# try to automate this - may need user help

echo "create truststores and import trusted certificates"

keytool -importcert -alias client -file client.pfx -keypass client -noprompt -storetype jks -keystore servertrust.keystore -storepass server

keytool -importcert -alias server -file server.pfx -keypass server -noprompt -storetype jks -keystore clienttrust.keystore -storepass client

#rm for now may need for group testing
rm -f client.pfx
rm -f server.pfx

# move ther stores into the client and server source folders
mv client.keystore clienttrust.keystore client/src
mv server.keystore servertrust.keystore server/src

echo "keystores, key pairs, and certificates set up"
echo "**** refer to README or wiki for requirements and specs ****"
