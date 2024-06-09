SRC_DIR = src
BIN_DIR = bin

JAVAC = javac
JAVAC_FLAGS = -d $(BIN_DIR) -sourcepath $(SRC_DIR)
JAVADOC = javadoc
JAVADOC_FLAGS = -d doc

MAIN_CLASS = ClubSimulation

JAVA_SOURCES = $(wildcard $(SRC_DIR)/*.java)
CLASSES = $(patsubst $(SRC_DIR)/%.java,$(BIN_DIR)/%.class,$(JAVA_SOURCES))

all: $(CLASSES)

$(BIN_DIR)/%.class: $(SRC_DIR)/%.java
	$(JAVAC) $(JAVAC_FLAGS) $<

run: 
	java -cp $(BIN_DIR) $(MAIN_CLASS) $(ARGS)

.PHONY: all run

javadoc:
	$(JAVADOC) $(JAVADOC_FLAGS) $(JAVA_SOURCES)
	
clean:
	find $(BIN_DIR) -name '*.class' -exec rm {} \;

.PHONY: clean
