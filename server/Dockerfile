#### Packages Server
# Note: The build context for this Dockerfile is at the project root, as maven requires the api-server's parent

### Build Scripts
# Windows build compatbility - sanitize Windows line endings and mark scripts as executable
FROM alpine:3.12.0 as scripts

# install dos2unix to remove \r's from scripts when compiling on Windows machines
RUN apk add --no-cache dos2unix
# copy updater script
COPY ./server/docker/*.sh /packages/server/scripts/
# remove \r's and set as executable
RUN dos2unix /packages/server/scripts/*.sh && \
    chmod +x /packages/server/scripts/*.sh


### Maven Precompiled Project Artifacts
# Compiles the cli-tool
FROM maven:3.6.3-openjdk-15 as unpacker

## Runtime configuration
# set current work directory
WORKDIR /packages/

## Copy Projects
# copy root Maven module manifest
COPY ./pom.xml ./lombok.config /packages/
# copy I/O api module
COPY ./api/ /packages/api/
# copy cli-tool module
COPY ./cli/ /packages/cli/

## Compile Projects
# compile project
# note: this layer will cache project dependencies in the same way that dependency:go-offline does
RUN DOCKER=true mvn package -Dstyle.color=always -Djansi.force=true


### Build Minimal JRE
# Build a minimal JRE based on the compiled module-info.class from the precompiled build stage
# Note: This stage needs an alpine-based JDK to build a JRE for the final alpine image
FROM openjdk:15-ea-jdk-alpine3.12 as jre

# copy module information
COPY --from=unpacker /packages/cli/target/unpacker.jar /packages/cli/unpacker.jar

# create custom minimal JRE
RUN jlink \
    --module-path "/packages/cli/" \
    --add-modules me.concision.unnamed.unpacker.cli \
    --output /packages/jre/


### Build Runtime Application
# Build Docker image with all project artifacts
FROM python:3.8.5-alpine3.12

## Image Configuration
# add JRE to path
ENV PATH="/opt/jre/bin:${PATH}"
# set current working directory
WORKDIR /packages/server/
# dynamic server data (e.g. configuration, logs, etc)
VOLUME /packages/server/data/
# set container "entrypoint"
CMD /packages/server/entrypoint.sh
# expose default HTTP port
EXPOSE 80

## Collect Linux Dependencies
# copy custom minimal JRE
COPY --from=jre /packages/jre/ /opt/jre/
# copy unpacker cli tool
COPY --from=unpacker /packages/cli/target/unpacker.jar /packages/cli/unpacker.jar

# install Wine
RUN apk add --no-cache \
    wine \
    # wine runtime dependencies for our Windows application
    libpng freetype

## install Wine and minimize based on runtime analysis
#RUN \
#    # take snapshot of filesystem before wine installation
#    find / -type f | sort > original-snapshot.txt &&
#    # install wine and dependencies
#    apk add --no-cache \
#        wine \
#        # wine runtime dependencies for our Windows application
#        libpng freetype && \
#    # take snapshot of filesystem after wine installation
#    find / -type f | sort > installed-snapshot.txt && \
#    # compute difference to discover all new wine files
#    comm -2 -3 installed-snapshot.txt original-snapshot.txt > wine-files.txt && \
#    # remove filesystem snapshots
#    rm -f installed-snapshot.txt original-snapshot.txt && \
#    # run unpacker using game updater
#    java -jar /packages/cli/unpacker.jar \
#        # use game updater to fetch latest
#        --source-type LATEST \
#        # output a raw binary Packages.bin
#        --output-format BINARY \
#        # set the default wine command
#        --wine-cmd "strace --follow-forks --quiet=all --trace=open --silence=all --no-abbrev --output=strace.txt wine64" \
#        # pipe the Packages.bin output to /dev/null
#        >/dev/null && \
#    # sanitize strace log
#    grep -o "\".*\"" strace.txt | tr -d "\"" | xargs realpath 2>/dev/null | grep -v "%" | uniq | sort > used-files.txt && \
#    # delete strace log
#    rm -f strace.txt && \
#    # compute difference to discover all unused wine files and delete them
#    comm -2 -3 wine-files.txt used-files.txt | xargs rm -f

## Project Artifacts
# copy Linux scripts
COPY --from=scripts /packages/server/scripts/*.sh /packages/server/