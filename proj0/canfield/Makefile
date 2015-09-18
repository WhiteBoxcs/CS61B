# This a Makefile, an input file for the GNU 'make' program.  For you 
# command-line and Emacs enthusiasts, this makes it possible to build
# this program with a single command:
#     make 
# You can also clean up junk files and .class files with
#     make clean
# To run style61b (our style enforcer) over your source files, type
#     make style
# Finally, you can run any tests you'd care to with
#     make check

SHELL = bash

STYLEPROG = style61b

PACKAGE = canfield

# Flags to pass to Java compilations (include debugging info and report
# "unsafe" operations.)
JFLAGS = -g -Xlint:unchecked -cp ..:$(CLASSPATH) -d .. -encoding utf8

SRCS = $(wildcard *.java)

CLASSES = $(SRCS:.java=.class)

# Tell make that these are not really files.
.PHONY: clean default compile style check unit integration 

# By default, make sure all classes are present and check if any sources have
# changed since the last build.
default: compile

compile: $(CLASSES)

style:
	$(STYLEPROG) $(SRCS) 

$(CLASSES): sentinel

# The sentinel file is a trick we use to accommodate Java's
# potentially mutually recursive compilation requirements.  'sentinel'
# is an empty file that keeps track of the time of the last
# compilation.  Whenever this is earlier than the date of one or more
# of the Java source files (or when the sentinel does not exist), the
# makefile command below forces a recompilation, and, if that is
# successful, updates the date on the sentinel file (with 'touch').

sentinel: $(SRCS)
	javac $(JFLAGS) $(SRCS)
	touch $@

# Run Tests.
check: 
	$(MAKE) -C .. check

# Run unit tests
unit:
	$(MAKE) -C .. unit

# Run integration tests
integration:
	$(MAKE) -C .. integration

# Find and remove all *~ and *.class files.
clean:
	$(RM) sentinel *.class 
	$(RM) *~

# Include additions to standard Makefile, if any.
-include Makefile.local
