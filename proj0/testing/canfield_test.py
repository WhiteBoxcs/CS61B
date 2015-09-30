import sys, re
from subprocess import Popen, PIPE, STDOUT, TimeoutExpired
from os.path import basename, splitext
from io import StringIO
from getopt import getopt, GetoptError
from difflib import context_diff

TIMEOUT = 3

verbose = False
log_file = None
try:
    opts, files = getopt(sys.argv[1:], 'v', [ 'verbose', 'log=' ])
    for opt, val in opts:
        if opt == '-v' or opt == '--verbose':
            verbose = True
        elif opt == '--log':
            verbose = True
            log_file = val
except GetoptError:
    print("Usage: python3 canfield_test.py [ -v | --verbose ] [ --log ] FILE...",
          file=sys.stderr)
    sys.exit(1)

ok = errors = 0

if log_file is not None:
    log = open(log_file, "w")
elif verbose:
    log = sys.stderr
else:
    log = None

def logprint(msg, **keys):
    print(msg, **keys)
    if log:
        print(msg, file=log, **keys)

def clean_input_line(line):
    line = re.sub(r'^(> )+', '', line)
    line = re.sub(r'^Another game\? \[yn\] *', '', line)
    return line

def clean_output_line(output):
    output = re.sub(r'^(> )+', '', output)
    output = re.sub(r'^(Another game\? \[yn\] *).*', r'\1', output)
    if output == '' or output[-1] != '\n':
        output = output + "\n"
    return output

for inp_file in files:
    logprint(basename(inp_file) + ": ", end='')
    with open(inp_file) as inp:
        seed = inp.readline().strip()
        transcript = inp.readlines()
        command_input = ''.join([clean_input_line(line) for line in transcript
                                 if re.match('> |#', line)])
        expected_output = [clean_output_line(line) for line in transcript
                           if not re.match('> |#', line)]
        
    proc = Popen('java -ea canfield.Main --text --seed={}'.format(seed),
                 stdin=PIPE, stdout=PIPE, stderr=STDOUT,
                 shell=True, universal_newlines=True)
    error = showdiff = False
    try: 
        output, err = proc.communicate(command_input, timeout=TIMEOUT)
    except TimeoutExpired:
        logprint("ERROR (timeout)")
        error = True
        proc.kill()
        output, err = proc.communicate('')
    if output and output[-1] == '\n':
        output = output[:-1]
    output = [ clean_output_line(line)
               for line in re.split(r'\n', output) ]
    if proc.returncode != 0:
        logprint("ERROR (crash)")
        error = True
    if output != expected_output:
        logprint("ERROR (wrong output)")
        error = showdiff = True

    if error:
        errors += 1
        if log:
            print("===== Input =====", file=log)
            print(command_input, end="", file=log)
            print("===== Expected Output =====", file=log)
            print(''.join(expected_output), end="", file=log)
            print("===== Actual Output =====", file=log)
            print(''.join(output), end="", file=log)
            if showdiff:
                print("===== Differences =====", file=log)
                print(''.join(context_diff(expected_output, output,
                                           "Expected", "Actual")), file=log)
            print("===== =====", file=log)
    else:
        logprint("OK")
        ok += 1

logprint("Summary: Passed {} of {} tests".format(ok, ok + errors))

sys.exit(errors)

      
        
