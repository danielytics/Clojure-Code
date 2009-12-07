#!/bin/sh
CP=lib/jar:lib/clj:src
for FILE in `ls jar`;
do
    CP="$CP:jar/$FILE"
done

java -cp $CP -Xmx512m -Xms512m -XX:MaxNewSize=24m -XX:NewSize=24m -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseConcMarkSweepGC  clojure.main src/sashimi.clj:

