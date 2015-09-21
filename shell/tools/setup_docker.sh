#!/bin/sh
if [ -z "$(which curl)" ] ; then 
    sudo apt-get update
    sudo apt-get install -y curl
fi 
echo "Starting to install docker via offical script:"
curl -ssL https://get.docker.com/ | sh

echo "Adding the user to the 'docker' group"
sudo usermod -aG docker $USER
