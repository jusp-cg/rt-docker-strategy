oc delete is,bc,dc,svc,route service3-dev
oc new-app https://bitbucket.capgroup.com/scm/cicd/ocpsamples.git --source-secret=bitbucket-read --context-dir=python-flask --name=service3-dev 
oc expose svc/service3-dev
pause
