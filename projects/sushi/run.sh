#!/bin/sh
CP=lib/jar:lib/clj:src
for FILE in `ls jar`;
do
    CP="$CP:jar/$FILE"
done
for FILE in `ls lwjgl/jar`;
do
    CP="$CP:lwjgl/jar/$FILE"
done

java -cp $CP -Djava.library.path=lwjgl/lib/ -Xmx512m -Xms512m -XX:MaxNewSize=24m -XX:NewSize=24m -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseConcMarkSweepGC  clojure.main src/snake.clj
#java -cp $CP -Djava.library.path=lwjgl/lib/  jline.ConsoleRunner clojure.lang.Repl src/snake.clj

