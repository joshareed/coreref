#!/bin/sh

DB="coreref"

mongo $DB --eval "db.getCollection('_collections').drop();"
mongoimport -d coreref -c _collections collections.json
