docker run -d \
           --name ecas.cc.cec.eu.int \
           --network host \
           -p 7001:7001 -p 7002:7002 -p 7003:7003 \
           is4stat/eulogin:6.2.7
