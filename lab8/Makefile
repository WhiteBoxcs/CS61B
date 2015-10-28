
.PHONY: compile check1 check2 check3 clean

SHELL = bash

SRCS = P1.java P2.java P3.java

CLASSES = $(SRCS:.java=.class)

%.class: %.java
	javac -g $<

compile: $(CLASSES)

check:
	$(MAKE) -k check1 check2 check3

check1: P1.class
	for f in 1/*.inp; do \
	    java P1 < $$f 2>&1 | tee OUT; \
	    if diff -b OUT $${f/.inp/.results}; then \
                echo $$f OK.; \
            else \
                echo $$f ERROR.; exit 1; \
            fi; \
	done

check2: P2.class
	for f in 2/*.inp; do \
	    java P2 < $$f 2>&1 | tee OUT; \
	    if diff -b OUT $${f/.inp/.results}; then \
                echo $$f OK.; \
            else \
                echo $$f ERROR.; exit 1; \
            fi; \
	done

check3: P3.class
	for f in 3/*.inp; do \
	    java P3 < $$f 2>&1 | tee OUT; \
	    if diff -b OUT $${f/.inp/.results}; then \
                echo $$f OK.; \
            else \
                echo $$f ERROR.; exit 1; \
            fi; \
	done

clean:
	$(RM) *.class *~ OUT
