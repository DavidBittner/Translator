C = javac
JAR = jar cfm

SRC = $(wildcard ./src/*.java)
CLASS = $(SRC:.java=.class)

BIN = csvtranslator_v3.3.jar

MANIFEST = Manifest

VTARGET = 7
RT = $(shell locate -r /rt.jar$)

all:
	@echo Compiling for target all...
	@$(C) -target $(VTARGET) -source $(VTARGET) $(SRC)
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
