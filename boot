#!/bin/bash


if [ -f ".project-env" ]; then
  . ".project-env"
fi

boot2 "$@"
