oc project app1-dev
mkdir cgcfg
cd cgcfg
oc export bc,is  --as-template='template' service3-dev > ocp-build.yml
oc export dc,svc,route --as-template='template' service3-dev > ocp-deploy.yml
cd ..
