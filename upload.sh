#!/bin/bash

rsync -avP --delete -e ssh updateSite telegraph_road,testful@web.sourceforge.net:htdocs
