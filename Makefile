C = javac
JAR = jar cfm

SRC = $(wildcard ./src/*.java)
CLASS = $(SRC:.java=.class)

BIN = csvtranslator_v2.2.jar

MANIFEST = Manifest

all:
	@echo Compiling for target all...
	@$(C) $(SRC)
	@echo All compiled.

jar: all
	@echo Jarring together class files...
	@cd src; $(JAR) $(BIN) $(MANIFEST) $(subst ./src/, ,$(CLASS))
	@mv ./src/$(BIN) .
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
