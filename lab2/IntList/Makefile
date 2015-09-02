#    make	      # Defaults to the first target: default
#    make default    # Same as gmake compile.
#    make compile    # Compiles Java code in this directory.
#    make style      # Runs style checks (only on instructional machines)
#    make check      # Compile if needed and test using IntListTest.
#    make clean      # Remove extraneous or rebuildable files

STYLE = style61b

# Define $(SRCS) to be a list of Java files.
SRCS = IntListTest.java IntList.java

# The targets following .PHONY aren't actually files; they serve as commands.
.PHONY: compile default check clean style

# By default, compile all sources
default: compile

# To compile all source files, just bring the file YearCheck.class up to date.
compile: IntListTest.class

# Run our style checks. Will only work on instructional machines
style:
	$(STYLE) -s lab2-suppress.xml $(SRCS)

# Test the compiled program, after first making sure it is up-to-date.
check: IntListTest.class
	java IntListTest 

# Remove extraneous or reconstructable files.
clean:
	rm -f *.class *~

IntListTest.class: $(SRCS) 
	javac -g $(SRCS)

