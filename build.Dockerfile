FROM openjdk:8

# Install sbt
RUN echo "deb http://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list \
 && apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 \
 && apt-get update \
 && apt-get install -y \
        sbt \
 && rm -rf /var/lib/apt/lists/*

# Create workspace
WORKDIR /work

# Force sbt to get dependencies
COPY build.sbt /work/build.sbt
COPY project /work/project
RUN sbt compile clean

# Stage dockerfile
COPY . /work
RUN sbt docker:stage
