#!/bin/bash

rsync -avP -e ssh updateSite telegraph_road,testful@web.sourceforge.net:htdocs
