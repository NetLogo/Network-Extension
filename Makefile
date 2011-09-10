ifeq ($(origin NETLOGO), undefined)
  NETLOGO=../..
endif

ifeq ($(origin SCALA_HOME), undefined)
  SCALA_HOME=../..
endif

SRCS=$(wildcard src/*.scala)

network.jar: $(SRCS) manifest.txt Makefile
	mkdir -p classes
	$(SCALA_HOME)/bin/scalac -deprecation -unchecked -encoding us-ascii -classpath $(NETLOGO)/NetLogo.jar -d classes $(SRCS)
	jar cmf manifest.txt network.jar -C classes .

network.zip: network.jar
	rm -rf network
	mkdir network
	cp -rp network.jar README.md Makefile src manifest.txt tests.txt network
	zip -rv network.zip network
	rm -rf network
