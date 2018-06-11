
MVN=mvn

SONAR_HOST=http://localhost:9999

sonar-analysis:
	# Running sonar analysis
	$(MVN) clean test -P coverage && \
	  $(MVN) org.jacoco:jacoco-maven-plugin:restore-instrumented-classes
	$(MVN) sonar:sonar -Dsonar.host.url=$(SONAR_HOST) \
		-Dsonar.tests="src/test"

clean:
	$(MVN) clean

verify:
	# Running tests
	$(MVN) clean test

install:
	$(MVN) clean install

build:
	# Building...
	$(MVN) clean package

deploy:
	# Deploying
	GPG_TTY=$$(tty) $(MVN) clean compile deploy -P release
