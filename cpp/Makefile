runTests: compile
	out/elections

compile: out
	g++ -std=c++20 -Isrc/ -Itests/ src/Elections.cpp tests/ElectionsTest.cpp -oout/elections

out:
	mkdir -p out/

clean:
	rm -rf out/
