C = javac
JAR = jar cfm

SRC = $(wildcard ./src/*.java)
CLASS = $(SRC:.java=.class)

BIN = Translator.jar

MANIFEST = ./src/Manifest

all:
	@echo Compiling for target all...
	@$(C) $(SRC)
	@echo All compiled.

jar: all
	@echo Jarring together class files...
	@$(JAR) $(BIN) $(MANIFEST) $(CLASS)
	@echo $(BIN) was created.

clean:
	@echo Deleting $(CLASS)
	@rm $(CLASS)
	@echo Deleting $(BIN)
	@rm $(BIN)

force:
	@echo Forcing recompile...
	@touch $(SRC)
	@make
