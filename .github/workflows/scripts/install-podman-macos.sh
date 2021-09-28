#!/bin/bash

ssh-keygen -q -t rsa -N '' <<< $'\ny' >/dev/null 2>&1
brew install podman
podman machine init
podman machine start
