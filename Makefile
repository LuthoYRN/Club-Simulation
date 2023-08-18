SRC_DIR = src
BIN_DIR = bin
PACKAGE_DIR = $(BIN_DIR)/clubSimulation

JAVAC = javac
JAVAC_FLAGS = -d $(BIN_DIR) -sourcepath $(SRC_DIR)
JAVADOC = javadoc
JAVADOC_FLAGS = -d doc

MAIN_CLASS = clubSimulation.ClubSimulation

JAVA_SOURCES = $(wildcard $(SRC_DIR)/clubSimulation/*.java)
CLASSES = $(patsubst $(SRC_DIR)/%.java,$(PACKAGE_DIR)/%.class,$(JAVA_SOURCES))

all: $(CLASSES)

$(PACKAGE_DIR)/%.class: $(SRC_DIR)/%.java
	@mkdir -p $(PACKAGE_DIR)
	$(JAVAC) $(JAVAC_FLAGS) $<

run: 
	java -cp $(BIN_DIR) $(MAIN_CLASS) $(ARGS)

.PHONY: all run

javadoc:
	$(JAVADOC) $(JAVADOC_FLAGS) $(JAVA_SOURCES)
	
clean:
	find $(PACKAGE_DIR) -name '*.class' -exec rm {} \;

.PHONY: clean
