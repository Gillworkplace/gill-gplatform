source /etc/bashrc && \
java -jar \
-Xmx${XMX} -Xms${XMS} \
-XX:+UseG1GC \
-XX:+PrintGCDetails \
-Xlog:gc:./logs/gc.log \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=./dump/HeapDumpOnOutOfMemoryError.dump \
${APP_ROOT}/*.jar