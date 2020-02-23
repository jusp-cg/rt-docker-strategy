REM Git Access
oc delete secret bitbucket-read
oc secret new-basicauth bitbucket-read --username=cicloneservice --password=%GIT_READ_PASSWORD%
oc secrets link builder bitbucket-read
