#!/bin/bash


if [ $# -ge 1 ]
then
    echo "Deploying app number $1"
    i=$1
    cd ~/my_apps
    onos-create-app app org.app${i} app${i} 1.7.0 org.app${i}
    cp ~/ONOS-Attack/attacks/scenario5/app${i}.java ~/my_apps/app${i}/src/main/java/org/app${i}/AppComponent.java
    cd app${i}
    python -c "tmp = open('$(pwd)/pom.xml','rb').read().replace('org.foo.app', 'org.app${i}').replace('Foo App', 'App${i}').replace('Foo, Inc.', 'L\'App${i}, cpny'); open('$(pwd)/pom.xml','wb').write(tmp)"
    mvn clean install -Dmaven.test.skip=true
    echo onos-app localhost install target/app${i}-1.7.0.oar
    onos-app localhost install target/app${i}-1.7.0.oar
else
    echo "No argument : deploying all apps !"
    for i in $(seq 9 10);
    do
        cd ~/my_apps
        onos-create-app app org.app${i} app${i} 1.7.0 org.app${i}
        cp ~/ONOS-Attack/attacks/scenario5/app${i}.java ~/my_apps/app${i}/src/main/java/org/app${i}/AppComponent.java
        cd app${i}
        python -c "tmp = open('$(pwd)/pom.xml','rb').read().replace('org.foo.app', 'org.app${i}').replace('Foo App', 'App${i}').replace('Foo, Inc.', 'L\'App${i}, cpny'); open('$(pwd)/pom.xml','wb').write(tmp)"
        mvn clean install -Dmaven.test.skip=true
        echo onos-app localhost install target/app${i}-1.7.0.oar
        onos-app localhost install target/app${i}-1.7.0.oar
    done
fi
