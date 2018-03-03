FROM openjdk:8-jdk-alpine

WORKDIR /tmp

COPY . .

# extract jar files from distribution zips
RUN apk add --update bash && rm -rf /var/cache/apk/* \
  && ./gradlew clean build -x findbugsMain -x findbugsTest \
      -x checkstyleMain -x checkstyleTest \
      -x pmdMain -x pmdTest \
      -x test -x processTestResources \
      -x compileTestJava -x testClasses \
      -x distTar \
  && mkdir -p /opt \
  && unzip -d /opt dat/build/distributions/*.zip \
  && unzip -d /opt dds/build/distributions/*.zip \
  && unzip -d /opt extract/build/distributions/*.zip \
  && ln -s /opt/dat-*/bin/dat /usr/bin/dat \
  && ln -s /opt/dds-*/bin/dds /usr/bin/dds \
  && ln -s /opt/extract-*/bin/extract /usr/bin/extract \
  && rm -rf /tmp/*

#/usr/bin
WORKDIR /usr/src/app

VOLUME ["/usr/src/app"]
#ENTRYPOINT ["java", "-jar", "/dat-reader.jar"]
