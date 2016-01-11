JC=javac

package_path=io/github/tpenguinltg/minesweeper/legacy

.PHONY: clean

all: ${package_path}/Cell.java ${package_path}/Minefield.java ${package_path}/Losemine.java
	${JC} ${package_path}/Cell.java ${package_path}/Minefield.java ${package_path}/Losemine.java
	jar -cfe losemine-legacy.jar io.github.tpenguinltg.minesweeper.legacy.Losemine io

clean:
	rm -f ${package_path}/*.class
	rm -f losemine-legacy.jar

