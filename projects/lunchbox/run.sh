#!/bin/sh
CP=lib/jar:lib/clj:src
for FILE in `ls lib`;
do
    CP="$CP:lib/$FILE"
done

# java -cp $CP jline.ConsoleRunner clojure.lang.Repl src/lunchbox.clj
java -cp $CP clojure.main src/lunchbox.clj $1

