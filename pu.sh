#!/usr/bin/env bash
git config --global user.email "travis@travis-ci.org"
git config --global user.name "Travis CI"

git clone https://${GH_TOKEN}@github.com/poeatlas/Content.ggpk.git

mkdir -p Content.ggpk/jars/
cp dat/build/libs/*.jar Content.ggpk/jars/
cp dds/build/libs/*.jar Content.ggpk/jars/
cd Content.ggpk

git add jars/*.jar
git commit --message "Travis build: $TRAVIS_BUILD_NUMBER"
git push origin master