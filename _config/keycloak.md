
# Add system user:

./add-user-keycloak.sh -r master -u admin -p admin


# Export Realm

## we must shutdown the KC server before export

./standalone.sh -Dkeycloak.migration.realmName=seed-app -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=dir -Dkeycloak.migration.dir=/home/gbuntu/Desktop/

