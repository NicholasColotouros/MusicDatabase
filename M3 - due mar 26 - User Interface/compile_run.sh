#!/bin/bash
javac -classpath postgresql.jar Q3UserInterface.java
java -classpath postgresql.jar:. Q3UserInterface