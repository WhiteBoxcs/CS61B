.PHONY: default dist

REMOTEDIR = $(REMOTE_PREFIX)

default:
	echo "Nothing to be done."

include ../lib/handout.Makefile

dist::
	rsync -av --delete --exclude .svn --exclude Makefile --exclude '*~' \
	     ../cs61b-software $(REMOTE_HOST):.
	rsync -a README $(REMOTEDIR)homesetup.README


