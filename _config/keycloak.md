
# Add system user:

./add-user-keycloak.sh -r master -u admin -p admin


# Export Realm

## we must shutdown the KC server before export

### export separate realm and user files into directory
./standalone.sh -Dkeycloak.migration.realmName=blogapp -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=dir -Dkeycloak.migration.dir=/home/gbuntu/Desktop/

### single file
./standalone.sh -Dkeycloak.migration.realmName=blogapp -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=/home/gbuntu/Desktop/keycloak-export.json 

