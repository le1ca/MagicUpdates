SOURCES = $(wildcard magic/*.java)
CLASSES = $(SOURCES:.java=.class)
TARGET  = magic.jar
CP      = .:./magic/:./resty-0.3.2.jar
JAVAC   = javac -cp "$(CP)"

$(TARGET): $(CLASSES) resty-0.3.2.jar
	mkdir -p build/magic
	mv $(CLASSES) build/magic
	unzip -qo resty-0.3.2.jar -d build
	rm -r build/META-INF
	cd build && jar cfe ../$(TARGET) magic.MagicUpdater ./*
	
%.class: %.java
	$(JAVAC) $<
	
clean:
	$(RM) $(TARGET) $(CLASSES)
	$(RM) -r build
