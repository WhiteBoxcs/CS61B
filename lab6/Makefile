# Makefile for Lab #4

#    make	     # Defaults to the first target: default
#    make default    # Same as make compile.
#    make compile    # Compile all sources
#    make clean      # Remove extraneous or rebuildable files
#    make style      # Run style checks.
#    make time       # Time Dups1, Dups2.

STYLEPROG = style61b

# Define $(SRCS) to be a list of Java files.
SRCS = utils/Filter.java utils/Predicate.java \
	TrivialFilter.java AlternatingFilter.java \
	TestFilters.java EveryOtherWord.java PredicateFilter.java \
	FilterClient.java

CLASSES = $(SRCS:.java=.class)

# Compiler flags for Java compilation

JFLAGS = -g -Xlint:unchecked

# The targets following .PHONY aren't actually files; they serve as commands.
.PHONY: compile default clean

# By default, compile all sources
default: compile

compile: $(CLASSES)

check:
	@echo "Make check isn't defined in the Makefile. You can add a definition that calls EveryOtherWord and TestFilters if you'd like. See lab5 for a description of how to add such a test to your makefile."

style:
	$(STYLEPROG) $(SRCS)

# Remove extraneous or reconstructable files.
clean:
	$(RM) *.class *~ utils/*.class utils/*~

$(CLASSES): $(SRCS)
	javac $(JFLAGS) $(SRCS)

