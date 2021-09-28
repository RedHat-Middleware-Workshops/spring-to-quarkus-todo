#!/bin/bash

brew install podman
podman machine init
podman machine start

ssh-add -l                                                                                                                    [10:03:13]