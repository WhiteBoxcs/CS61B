import sys, re
from subprocess import \
     check_output, PIPE, STDOUT, DEVNULL, CalledProcessError, TimeoutExpired
from os.path import abspath, basename, dirname, exists, join, splitext
from getopt import getopt, GetoptError
from os import chdir, environ, getcwd, mkdir, remove
from shutil import copyfile, rmtree
from math import log

SHORT_USAGE = """\
Usage: python3 tester.py OPTIONS TEST.in ...

   OPTIONS may include
       --show=N       Show details on up to N tests.
       --show=all     Show details on all tests.
       --keep         Keep test directories
       --progdir=DIR  Directory or JAR files containing gitlet application
       --timeout=SEC  Default number of seconds allowed to each execution
                      of gitlet.
       --verbose      Print extra information about execution.
"""

USAGE = SHORT_USAGE + """\

For each TEST.in, change to an empty directory, and execute the instructions
in TEST.in.  These instructions have one of the following forms:

   T N    Set the timeout for gitlet commands in the rest of this test to N
          seconds.
   + NAME F
          Copy the contents of src/F into a file named NAME.
   - NAME
          Delete the file named NAME.
   > COMMAND OPERANDS
   LINE1
   LINE2
   ...
   <<<
          Run gitlet.Main with COMMAND ARGUMENTS as its parameters.  Compare
          its output with LINE1, LINE2, etc., reporting an error if there is
          "sufficient" discrepency.  The <<< delimiter may be followed by
          an asterisk (*), which case, the preceding lines are treated as 
          Python regular expressions and matched accordingly. The directory
          or JAR file containing the gitlet.Main program is assumed to be
          in directory DIR specifed by --progdir (default is ..).
   = NAME F
          Check that the file named NAME is identical to src/F, and report an
          error if not.
   * NAME
          Check that the file NAME does not exist, and report an error if it
          does.
   E NAME
          Check that file or directory NAME exists, and report an error if it
          does not.

For each TEST.in, reports at most one error.  Without the --show option,
simply indicates tests passed and failed.  If N is postive, also prints details
of the first N failing tests. With --show=all, shows details of all failing
tests.  With --keep, keeps the directories created for the tests (with names
TEST.dir).

When finished, reports number of tests passed and failed, and the number of
faulty TEST.in files."""

OUTPUT_TOLERANCE = 2
GITLET_COMMAND = "java gitlet.Main"
TIMEOUT = 5

def Usage():
    print(SHORT_USAGE, file=sys.stderr)
    sys.exit(1)

Mat = None
def Match(patn, s):
    global Mat
    Mat = re.match(patn, s)
    return Mat

def Group(n):
    return Mat.group(n)

def contents(filename):
    try:
        with open(filename) as inp:
            return inp.read()
    except FileNotFoundError:
        return None

def editDistance(s1, s2):
    dist = [list(range(len(s2) + 1))] + \
           [ [i] + [ 0 ] * len(s2) for i in range(1, len(s1) + 1) ]
    for i in range(1, len(s1) + 1):
        for j in range(1, len(s2) + 1):
            dist[i][j] = min(dist[i-1][j] + 1,
                             dist[i][j-1] + 1,
                             dist[i-1][j-1] + (s1[i-1] != s2[j-1]))
    return dist[len(s1)][len(s2)]

def createTempDir(base):
    for n in range(100):
        name = "{}_{}".format(base, n)
        try:
            mkdir(name)
            return name
        except OSError:
            pass
    else:
        raise ValueError("could not create temp directory for {}".format(base))

def cleanTempDir(dir):
    rmtree(dir, ignore_errors=True)

def doDelete(name, dir):
    try:
        remove(join(dir, name))
    except OSError:
        pass

def doCopy(dest, src, dir):
    try:
        doDelete(dest, dir)
        copyfile(join('src', src), join(dir, dest))
    except OSError:
        raise ValueError("file {} could not be copied to {}".format(src, dest))

def doExecute(cmnd, dir, timeout):
    here = getcwd()
    try:
        chdir(dir)
        full_cmnd = "{} {}".format(GITLET_COMMAND, cmnd)
        out = check_output(full_cmnd, shell=True, universal_newlines=True,
                           stdin=DEVNULL, stderr=STDOUT, timeout=timeout)
        return "OK", out
    except CalledProcessError as excp:
        return "java gitlet.Main exited with code {}".format(excp.args[0]), None
    except TimeoutExpired:
        return "timeout", None
    finally:
        chdir(here)

def canonicalize(s):
    if s is None:
        return None
    return re.sub('\r', '', s)

def fileExists(f, dir):
    return exists(join(dir, f))

def correctFileOutput(name, expected, dir):
    userData = canonicalize(contents(join(dir, name)))
    stdData = canonicalize(contents(join('src', expected)))
    return userData == stdData

def correctProgramOutput(expected, actual, is_regexp):
    actual = re.split(r'\n\r?', actual.rstrip())
    expectedLen = len(expected)
    for i in range(len(expected) - 1, -1, -1):
        if Match(r'\s*$', expected[i]):
            expectedLen = i
    actualLen = len(actual)
    for i in range(len(actual) - 1, -1, -1):
        if Match(r'\s*$', actual[i]):
            actualLen = i
    if expectedLen != actualLen:
        return False
    for e, a in zip(expected[:expectedLen], actual[:actualLen]):
        if (is_regexp and not Match(e.strip() + "$", a.strip())) \
           or (not is_regexp and
               editDistance(e.strip(), a.strip()) > OUTPUT_TOLERANCE):
            return False
    return True

def reportDetails(test, n):
    if show is None:
        return
    if show == 0:
        print("   Limit on error details exceeded.")
        return
    base = basename(test)
    
    print("    Error on line {} of {}".format(n, base))
    print(("-" * 20 + " {} " + "-" * 20).format(base))
    fmt = "{{!{}d}}. {{}}".format(round(log(len(text), 10)))
    text = '\n'.join(map(lambda p: fmt.format(p[0] + 1, p[1]),
                         enumerate(re.split(r'[\n\r]+', contents(test)))))
    print(contents(test), end="")
    print("-" * (42 + len(base)))

def doTest(test):
    if not exists(test):
        return
    base = splitext(basename(test))[0]
    print("{}:".format(base), end=" ")
    tmpdir = createTempDir(base)
    if verbose:
        print("Testing directory: {}", tmpdir)
    timeout = TIMEOUT

    try:
        with open(test) as inp:
            n = 0
            while True:
                n += 1
                line = inp.readline()
                if line == "":
                    print("OK")
                    return True
                if verbose:
                    print()
                    print("+ {}".format(line.rstrip()), end=" ")
                if Match(r'\s*#', line):
                    pass
                elif Match(r'T\s*(\S+)', line):
                    try:
                        timeout = float(Group(1))
                    except:
                        ValueError("bad time: {}".format(line))
                elif Match(r'\+\s*(\S+)\s+(\S+)', line):
                    doCopy(Group(1), Group(2), tmpdir)
                elif Match(r'-\s*(\S+)', line):
                    doDelete(Group(1), tmpdir)
                elif Match(r'>\s*(.*)', line):
                    cmnd = Group(1)
                    expected = []
                    while True:
                        n += 1
                        L = inp.readline()
                        if L == '':
                            raise ValueError("unterminated command: {}"
                                             .format(line))
                        L = L.rstrip()
                        if Match(r'<<<(\*?)', L):
                            is_regexp = Group(1)
                            break
                        expected.append(L)
                    msg, out = doExecute(cmnd, tmpdir, timeout)
                    if verbose:
                        print(re.sub(r'(?m)^', '- ', out), end=" ")
                    if msg == "OK":
                        if not correctProgramOutput(expected, out, is_regexp):
                            msg = "incorrect output"
                    if msg != "OK":
                        print("ERROR ({})".format(msg))
                        reportDetails(test, n)
                        return False
                elif Match(r'=\s*(\S+)\s+(\S+)', line):
                    if not correctFileOutput(Group(1), Group(2), tmpdir):
                        print("ERROR (file {} has incorrect content)"
                              .format(Group(1)))
                        reportDetails(test, n)
                        return False
                elif Match(r'\*\s*(\S+)', line):
                    if fileExists(Group(1), tmpdir):
                        print("ERROR (file {} present)".format(Group(1)))
                        reportDetails(test, n)
                        return False
                elif Match(r'E\s*(\S+)', line):
                    if not fileExists(Group(1), tmpdir):
                        print("ERROR (file or directory {} not present)"
                              .format(Group(1)))
                        reportDetails(test, n)
                        return False
                else:
                    raise ValueError("FAILED (bad test line: {})".format(n))
    finally:
        if not keep:
            cleanTempDir(tmpdir)

if __name__ == "__main__":
    show = None
    keep = False
    prog_dir = None
    verbose = False

    try:
        opts, files = getopt(sys.argv[1:], '',
                             ['show=', "keep", "progdir=", "verbose" ])
        for opt, val in opts:
            if opt == '--show':
                show = int(val)
            elif opt == "--keep":
                keep = True
            elif opt == "--progdir":
                prog_dir = val
            elif opt == "--verbose":
                verbose = True
        if prog_dir is None:
            prog_dir = dirname(abspath(getcwd()))
        else:
            prog_dir = abspath(prog_dir)
    except GetoptError:
        Usage()
    if not files:
        print(USAGE)
        sys.exit(0)

    ON_WINDOWS = Match(r'.*\\', join('a', 'b'))
    if ON_WINDOWS:
        environ['CLASSPATH'] = "{};{}".format(prog_dir, environ['CLASSPATH'])
    else:
        environ['CLASSPATH'] = "{}:{}".format(prog_dir, environ['CLASSPATH'])
        GITLET_COMMAND = 'exec ' + GITLET_COMMAND

    num_tests = len(files)
    errs = 0
    fails = 0

    for test in files:
        try:
            if not doTest(test):
                errs += 1
                if type(show) is int:
                    show -= 1
        except ValueError as excp:
            print("FAILED ({})".format(excp.args[0]))
            fails += 1
                  
