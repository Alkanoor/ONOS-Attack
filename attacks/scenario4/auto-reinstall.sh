#!/bin/bash


if [ $# -ge 1 ]
then
    echo "Reinstalling app number $1"
    echo onos-app localhost install target/app${i}-1.7.0.oar
    onos-app localhost install target/app${i}-1.7.0.oar
else
    echo "No argument : deploying all apps !"
    for i in $(seq 1 8);
    do
        echo "Reinstalling app number ${i}"
        echo onos-app localhost install target/app${i}-1.7.0.oar
        onos-app localhost install target/app${i}-1.7.0.oar
    done
fi
