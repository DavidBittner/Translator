#!/bin/bash
java -jar trans.jar -import import -template Functions/template -export Functions/output
java -jar trans.jar -import import -template LargeFile/template -export LargeFile/output
java -jar trans.jar -import import -template Logic/template -export Logic/output
java -jar trans.jar -import import -template Lookup/template -export Lookup/output
java -jar trans.jar -import import -template NestedFunctions/template -export NestedFunctions/output
java -jar trans.jar -import import -template NestedLogic/template -export NestedLogic/output
