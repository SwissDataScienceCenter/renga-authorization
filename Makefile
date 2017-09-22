ifndef PLATFORM_VERSION
	PLATFORM_VERSION = latest
endif

sbt-targets += target/docker/stage

.PHONY: service
service: $(sbt-targets)
	@docker build -t renga-authorization:$(PLATFORM_VERSION) target/docker/stage

$(sbt-targets): $(shell find app conf project) build.sbt swagger.yml
	@mkdir -p target/docker/stage
	@docker build -t renga-authorization-build:$(PLATFORM_VERSION) -f build.Dockerfile .
	@ID=$$(docker create renga-authorization-build:$(PLATFORM_VERSION)); docker cp $$ID:/work/target/docker/stage target/docker/; docker rm $$ID

.PHONY: clean
clean:
	rm -rf target/*
