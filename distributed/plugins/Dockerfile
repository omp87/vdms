# Pull base image.
FROM ubuntu:20.04 as plugin_base
#needed so jdk installs without prompting user for response
ENV DEBIAN_FRONTEND=noninteractive

#Install Packages
RUN apt-get update && apt-get -y install maven g++ cmake python3 python3-pip openjdk-11-jdk-headless libprotobuf-java git protobuf-compiler curl unzip && cd / && git clone https://github.com/google/protobuf.git && cd /protobuf/cmake/ && mkdir build && cd build &&  cmake -Dprotobuf_BUILD_TESTS=OFF .. && make -j8 && cp protoc ../../src/protoc && cd /protobuf/java/core && mvn package && cp $(ls target/protobuf-java*.jar) /usr/share/java/protobuf.jar


RUN cd / && curl https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple/json-simple-1.1.1.jar -o json-simple-1.1.1.jar && mv json-simple-1.1.1.jar /usr/share/java && curl https://mirrors.gigenet.com/apache//commons/codec/binaries/commons-codec-1.15-bin.tar.gz -o commons-codec-1.15-bin.tar.gz && gunzip commons-codec-1.15-bin.tar.gz && tar -xf commons-codec-1.15-bin.tar && mv commons-codec-1.15/commons-codec-1.15.jar /usr/share/java


RUN cd / && git clone https://github.com/omp87/vdms.git && cd vdms && git checkout distributed_vdms && cd distributed/plugins && mkdir build && cd build && cmake .. && make -j16 

WORKDIR /vdms/distributed/plugins/build
ENTRYPOINT ["java", "-cp", "TestPlugin.jar:/usr/share/java/protobuf.jar:/usr/share/java/commons-codec-1.15.jar:/usr/share/java/json-simple-1.1.1.jar", "TestPlugin", "settings/replication_plugin_config.json"]
#ENTRYPOINT ["tail", "-f"]