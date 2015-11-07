;; gjdb.el:  GNU Java Debugger Interface support

;; Author: P. N. Hilfinger
;; The contents of this file are an enhancement of Emacs, and hence subject
;; to the terms of the GNU General Public License (see M-x describe-copying).

;; This file uses and is adapted from the file gud.el 
;; (Author: Eric S. Raymond <esr@snark.thyrsus.com>), which is
;; distributed with Emacs.

(require 'gud)
(require 'cc-engine)
(require 'cl)


;; ======================================================================
;; gjdb functions


(defvar gjdb-history nil "List of command lines passed to gjdb.")

(defvar gjdb-source-to-command-map nil
  "Association list mapping class names to gjdb commands used to debug them")

;;; Name of gjdb program
(defvar gjdb-program "gjdb"
  "*Name of the gjdb program.")

(defun gjdb-shell-subst (s)
  (let ((s-subst (substitute-env-vars s)))
    (if (string-match-p "^~.*/" s-subst)
        (expand-file-name s-subst)
      s-subst)))

(defun gjdb-massage-args (file args)
  (cons "-f" (map 'list 'gjdb-shell-subst args)))

(defvar gjdb-marker-regexp
    (concat "\032\032\\([^" path-separator "\n]*\\)" path-separator
	    "\\([0-9]*\\)" path-separator ".*\n"))

(defvar gjdb-use-function-keys-p t
  "*If non-nil, bind function keys to some of the common gjdb commands.")

(defvar gjdb-local-map nil
  "The local key map: simply an efficiency measure, to avoid reconstruction
of the local key map each time a GJDB-related buffer is entered.")
(make-variable-buffer-local 'gjdb-local-map)

(defmacro gjdb-def (func cmd key &optional doc)
  "Define FUNC to be a command sending STR and bound to KEY, with
optional doc string DOC.  Certain %-escapes in the string arguments
are interpreted specially if present.  These are:

  %c    class surrounding point in current class file.
  %f	name (without directory) of current source file.
  %F	name (without directory or extension) of current source file.
  %d	directory of current source file.
  %l	number of current source line
  %e    If the region is active, then the expression consisting of the 
        contents of the region, with newlines changed to blanks.  Otherwise,
  	the text of the C lvalue or function-call expression surrounding 
        point.
  %a	text of the hexadecimal address surrounding point
  %p	prefix argument to the command (if any) as a number

  The `current' source file is the file of the current buffer (if
we're in a C file) or the source file current at the last break or
step (if we're in the GUD buffer).
  The `current' line is that of the current buffer (if we're in a
source file) or the source line number at the last break or step (if
we're in the GUD buffer)."
  (list 'progn
	(list 'defun func '(arg)
	      (or doc "")
	      '(interactive "p")
	      (list 'gjdb-call cmd 'arg))
	(if key
	    (list 'define-key
		  '(current-local-map)
		  (concat "\C-c" key)
		  (list 'quote func)))))

;; There's no guarantee that Emacs will hand the filter the entire
;; marker at once; it could be broken up across several strings.  We
;; might even receive a big chunk with several markers in it.  If we
;; receive a chunk of text which looks like it might contain the
;; beginning of a marker, we save it here between calls to the
;; filter.
(defvar gud-marker-acc "")
(make-variable-buffer-local 'gud-marker-acc)

(defun gjdb-marker-filter (string)
  (setq gud-marker-acc (concat gud-marker-acc string))
  (let ((output ""))

    ;; Process all the complete markers in this chunk.
    (while (string-match gjdb-marker-regexp gud-marker-acc)
      (setq

	  ;; Extract the frame position from the marker.
	  gud-last-frame
	(let ((file-name 
	       (substring gud-marker-acc (match-beginning 1) (match-end 1)))
	      (line-num
	       (string-to-number (substring gud-marker-acc
					    (match-beginning 2)
					    (match-end 2)))))
	  (if (file-readable-p file-name)
	      (cons file-name line-num)
	    gud-last-frame))


	;; Append any text before the marker to the output we're going
	;; to return - we don't include the marker in this text.
	output (concat output
		       (substring gud-marker-acc 0 (match-beginning 0)))

	;; Set the accumulator to the remaining text.
	gud-marker-acc (substring gud-marker-acc (match-end 0))))

    ;; Does the remaining text look like it might end with the
    ;; beginning of another marker?  If it does, then keep it in
    ;; gud-marker-acc until we receive the rest of it.  Since we
    ;; know the full marker regexp above failed, it's pretty simple to
    ;; test for marker starts.
    (if (string-match "\032.*\\'" gud-marker-acc)
	(progn
	  ;; Everything before the potential marker start can be output.
	  (setq output (concat output (substring gud-marker-acc
						 0 (match-beginning 0))))

	  ;; Everything after, we save, to combine with later input.
	  (setq gud-marker-acc
	    (substring gud-marker-acc (match-beginning 0))))

      (setq output (concat output gud-marker-acc)
	    gud-marker-acc ""))

    output))

(defun gjdb-make-local-map ()
  "A new key map with an initially empty [menu-bar debug] entry."
  (setq gjdb-local-map 
    (if (current-local-map) 
	(copy-keymap (current-local-map)) 
      (make-sparse-keymap)))
  (define-key gjdb-local-map [menu-bar debug]
    (cons "Debug" (make-sparse-keymap "Debug"))))

(defun gjdb-set-local-sourcefile-map ()
  (if (null gjdb-local-map) 
      (progn
	(gjdb-make-local-map)
	(use-local-map gjdb-local-map)

	(if gjdb-use-function-keys-p 
	    (progn
	      (local-set-key [f3] 'gjdb-up)
	      (local-set-key [f4] 'gjdb-down)
	      (local-set-key [f5] 'gjdb-step)
	      (local-set-key [f6] 'gjdb-next)
	      (local-set-key [f7] 'gjdb-finish)
	      (local-set-key [f8] 'gjdb-cont)
	      (local-set-key [f9] 'gjdb-print)
	      (local-set-key [\S-f9] 'gjdb-dump)))

	(local-set-key "\C-x " 'gjdb-break)
	(mapc #'(lambda (x) 
		(local-set-key (concat gud-key-prefix (car x)) (cadr x)))
	      '(("\C-b" gjdb-break) ("\C-d" gjdb-remove) 
		("\C-p" gjdb-print)
		("<" gjdb-up) (">" gjdb-down) 
		("\C-f" gjdb-finish) ("\C-n" gjdb-next) ("\C-s" gjdb-step)
		("\C-r" gjdb-cont)))
			    
	(gjdb-menu-item 'start "Start Debugger" 'gjdb-from-source)
	(gjdb-menu-item 'clear "Clear Breakpoint" 'gjdb-remove)
	(gjdb-menu-item 'break "Set Breakpoint" 'gjdb-break)
	(gjdb-menu-item 'dump  "Print Details" 'gjdb-dump)
	(gjdb-menu-item 'eval  "Print" 'gjdb-print)
	(gjdb-menu-item 'continue "Continue" 'gjdb-cont)
	(gjdb-menu-item 'finish "Finish Function" 'gjdb-finish)
	(gjdb-menu-item 'next "Step Over" 'gjdb-next)
	(gjdb-menu-item 'step "Step Into" 'gjdb-step)
	(gjdb-menu-item 'down "View Callee" 'gjdb-down)
	(gjdb-menu-item 'up "View Caller" 'gjdb-up))
    (use-local-map gjdb-local-map)))

(defun gjdb-find-file (f)
  (save-excursion
    (let ((buf (find-file-noselect f)))
      (set-buffer buf)
      (gjdb-set-local-sourcefile-map)
      buf)))

(defvar gjdb-minibuffer-local-map nil
  "Keymap for minibuffer prompting of gjdb startup command.")
(if gjdb-minibuffer-local-map
    ()
  (setq gjdb-minibuffer-local-map (copy-keymap minibuffer-local-map))
  (define-key
      gjdb-minibuffer-local-map "\C-i" 'comint-dynamic-complete-filename))

;; Adapted from gud-call.
(defun gjdb-call (fmt &optional arg)
  (let ((msg (gjdb-format-command fmt arg)))
    (message "Command: %s" msg)
    (sit-for 0)
    (gud-basic-call msg)))

(defun gjdb-refresh (&optional arg)
  "Redisplay debugging buffer and source buffer."
  (interactive "P")
  (or gud-last-frame (setq gud-last-frame gud-last-last-frame))
  (gud-display-frame)
  (recenter arg)
)

;;; Enhancement of gud-format-command to handle %c (class). 
;;; The gud-call function must do the right thing whether its invoking
;;; keystroke is from the GUD buffer itself (via major-mode binding)
;;; or a C buffer.  In the former case, we want to supply data from
;;; gud-last-frame.  Here's how we do it:

(defun gjdb-format-command (str arg)
  (let ((insource (not (eq (current-buffer) gud-comint-buffer)))
	(frame (or gud-last-frame gud-last-last-frame))
	result)
    (while (and str (string-match "\\([^%]*\\)%\\([acdeflp]\\)" str))
      (let ((key (string-to-char (substring str (match-beginning 2))))
	    (prefix-start (match-beginning 1))
	    (prefix-end (match-end 1))
	    (suffix-start (match-end 2))
	    subst)
	(cond
	 ((eq key ?c)
	  (setq subst (gjdb-class-from-source-posn
		       (if insource (buffer-file-name) (car frame))
		       (if insource
			   (save-excursion
			     (beginning-of-line)
			     (save-restriction (widen) 
					       (1+ (count-lines 1 (point)))))
			 (cdr frame)))))
	 ((eq key ?f)
	  (setq subst (file-name-nondirectory (if insource
						  (buffer-file-name)
						(car frame)))))
	 ((eq key ?d)
	  (setq subst (file-name-directory (if insource
					       (buffer-file-name)
					     (car frame)))))
	 ((eq key ?l)
	  (setq subst (int-to-string (if insource
					 (save-excursion
					   (beginning-of-line)
					   (save-restriction 
					     (widen) 
					     (1+ (count-lines 1 (point)))))
				       (cdr frame)))))
	 ((eq key ?e)
	  (setq subst (gjdb-find-expr)))
	 ((eq key ?a)
	  (setq subst (gud-read-address)))
	 ((eq key ?p)
	  (setq subst (if arg (int-to-string arg) ""))))
	(setq result (concat result
			     (substring str prefix-start prefix-end)
			     subst))
	(setq str (substring str suffix-start))))
    ;; There might be text left in STR when the loop ends.
    (concat result str)))

(defun gjdb-find-expr ()
  (condition-case ()
      (let ((str (buffer-substring (region-beginning) (region-end))))
	(while (string-match "\n" str)
	  (setq str (concat (substring str 0 (match-beginning 1)) " "
			    (substring str (1+ (match-end 1))))))
	str)
    (error 
     (save-excursion
       (re-search-backward "[^.a-zA-Z_$0-9]" nil 'noerr)
       (if (re-search-forward "[.a-zA-Z_$0-9]+" nil t)
	   (match-string 0)
	 "")))))

(defun gjdb-class-search-limit (state)
  (save-excursion
    (let ((lim (cond ((null (cdr state)) 1)
		    ((numberp (cadr state)) (cadr state))
		    (t (cdr (cadr state))))))
      (while (and 
	      (search-backward ";" lim 'saturate)
	      (c-in-literal))
	nil)
      (point))))

(defun gjdb-get-containing-class-name () "
The name of the class containing point.  Anonymous inner classes have the 
form [<package>.]<outerclass>.0"

  (save-excursion
    (let ((state0 (c-parse-state))
	  state result type)

      ;; Set 'type to 'anonymous if in an anonymous inner class, 'named if
      ;; in interface or class, or null if neither.
      (setq state state0)
      (while (and state (null type))      
	(if (numberp (car state))
	    (progn 
	      (goto-char (car state))
	      (let ((lim (gjdb-class-search-limit state))
		    done)
		(while 
		    (and 
		     (null done)
		     (re-search-backward 
		      "\\b\\(new\\|class\\|interface\\)[ \n\t]+\\([a-zA-Z_0-9]+\\)\\|[;{}]"
		      lim t))
		  (let ((matched (match-string 1)))
		    (cond ((c-in-literal))
			  ((null matched) (setq done t))
			  ((equal matched "new") 
			   (setq type 'anonymous done t))
			  (t (setq type 'named done t))))))))
	(setq state (cdr state)))

      ;; Set 'result to name of enclosing type
      (setq state state0)
      (while state
	(if (numberp (car state))
	    (progn 
	      (goto-char (car state))
	      (let ((lim (gjdb-class-search-limit state))
		    id)	       
		(while 
		    (and 
		     (null id)
		     (re-search-backward 
		      "\\b\\(class\\|interface\\)[ \n\t]+\\([a-zA-Z_0-9]+\\)"
		      lim t))
		  (let ((matched (match-string 2)))
		    (if (not (c-in-literal))
			(setq id matched))))
		(setq result 
		      (cond ((eq type 'anonymous) id)
			    (result (concat id "." result))
			    (t id))))))
	(setq state (cdr state)))

      (cond ((null result) nil)
	    ((eq type 'anonymous) 
	     (concat (gjdb-get-package) result ".0"))
	    (t (concat (gjdb-get-package) result))))))

(defun gjdb-get-some-class-name ()
  (or (gjdb-get-containing-class-name)
      (let ((name (get-default-class-from-name (buffer-file-name))))
	(and name (concat (gjdb-get-package) name)))))

(defun gjdb-get-package () 
  "Package name associated with current source file, or nil for the anonymous
package."
  (save-excursion
    (let ((package nil))
      (goto-char (point-min))
      (while (and (not package)
		  (re-search-forward 
		   "\\bpackage[ \t\n]+\\([.a-zA-z0-9_]+\\)[ \t\n]*;" 
		   nil t))
	(let ((matched (match-string 1)))
	  (if (not (c-in-literal))
	      (setq package (concat matched ".")))))
      package)))

(defun get-default-class-from-name (filename)
  (if (string-match "\\([^/.]+\\)\.java$" filename)
      (match-string 1 filename)
    nil))

(defun gjdb-class-from-source-posn (filename lineno)
  "Return name of class containing the source in FILENAME at line number
LINENO."
  (let ((default-class (get-default-class-from-name filename))
	(lineno-str (int-to-string lineno)))
    (if (file-readable-p filename)
	(save-excursion
	  (progn
	    (set-buffer (find-file-noselect filename))
	    (save-excursion 
              (goto-char (point-min)) 
              (forward-line (1- lineno))
	      (let ((name (gjdb-get-containing-class-name)))
		(or name default-class
		    (error "Cannot determine class name"))))))
      (or default-class (error "Cannot determine class name")))))

(defun function-key-p (key-seq)
  "True if KEY-SEQ is a sequence starting with f1-f12"
  (and (vectorp key-seq) (> (length key-seq) 0) 
       (member (elt key-seq 0) '(f1 f2 f3 f4 f5 f6 f7 f8 f9 f10 f11 f12))))

(defun ascii-keys-p (key-seq)
  "True if KEY-SEQ is a sequence starting with an ascii character, possibly
modified by Ctrl or Meta."
  (and (vectorp key-seq) (> (length key-seq) 0) (integerp (elt key-seq 0))))

(defun describe-accel (bindings)
  "
A string describing the list of key sequences BINDINGS, suitable for use 
as the descriptions of possible accelerators in a menu item."
  (let (result L)
    (setq result " ")
    (setq L bindings)
    (while L 
      (if (function-key-p (car L))
	  (setq result (concat result " (" (key-description (car L)) ")")
		L nil)
	(setq L (cdr L))))
    (setq L bindings)
    (while L
      (if (ascii-keys-p (car L))
	  (setq result (concat result " (" (key-description (car L)) ")")
		L nil)
	(setq L (cdr L))))
    (if (string-equal result " ") nil result)))

(defun gjdb-menu-item (sym label command)
  "
Add local binding [menu-bar debug SYM] with label LABEL bound to COMMAND."
  (let* ((other-bindings (where-is-internal command))
	 (desc (describe-accel other-bindings)))
    (if desc
	(local-set-key `[menu-bar debug ,sym] 
		       `(,label (,(car other-bindings) . 
				 ,(describe-accel other-bindings))
				. ,command))
      (local-set-key `[menu-bar debug ,sym] `(,label . ,command)))))

;; Adapted from gud-common-init.
;; The first arg is the specified command line,
;; which starts with the program to debug.
;; The other three args specify the values to use
;; for local variables in the debugger buffer.
(defun gjdb-init (command-line)
  (let* ((words (split-string command-line))
	 (default-directory0 default-directory)
	 (program (car words))
	 ;; Extract the class name from WORDS
	 (args (cdr words))
	 (class-name (if (null args) "" (car (last args))))
	 (restart t))

    (set-buffer (get-buffer-create (concat "*gud-" class-name "*")))
    (if (get-buffer-process (current-buffer))
	(if (y-or-n-p "Terminate current session first? ")
	    (gjdb-clean-buffer-process)
	  (setq restart nil)))
    (if restart 
	(progn 
	  (or (bolp) (newline))
	  (if (and (not (equal default-directory default-directory0))
		   (y-or-n-p (concat "Switch to directory " 
				     default-directory0 "? ")))
	      (setq default-directory default-directory0))
	  (insert "Current directory is " default-directory "\n")
	  (apply 'make-comint (concat "gud-" class-name) program nil
		 (gjdb-massage-args nil args))
	  (gud-mode)
          (local-set-key "\t" 'comint-dynamic-complete)
          (local-set-key "\M-?" 'comint-dynamic-list-filename-completions)
	  (make-local-variable 'gud-marker-filter)
	  (setq gud-marker-filter 'gjdb-marker-filter)
	  (make-local-variable 'gud-find-file)
	  (setq gud-find-file 'gjdb-find-file)

	  (set-process-filter (get-buffer-process (current-buffer)) 
			      'gud-filter)
	  (set-process-sentinel (get-buffer-process (current-buffer)) 
				'gud-sentinel)))
    (gud-set-buffer))

  (if (null gjdb-local-map)
      (progn
	(gjdb-make-local-map)
	(use-local-map gjdb-local-map)

	(gjdb-def gjdb-break  "break %c:%l" nil 
		 "Set breakpoint at current line")
	(gjdb-def gjdb-remove "clear %c:%l" nil 
		 "Remove breakpoint at current line")  
	(gjdb-def gjdb-intr   "\006"   "\C-c" "Interrupt")
	(gjdb-def gjdb-step   "step %p"      "\C-s" 
		 "Step one source line.")
	(gjdb-def gjdb-next   "next %p"      "\C-n" 
		 "Step one line (skip functions).")
	(gjdb-def gjdb-cont   "cont"         "\C-r" 
		 "Continue program.")
	(gjdb-def gjdb-finish "finish"       "\C-f" 
		 "Finish executing current function.")
	(gjdb-def gjdb-up     "up %p"        "<" 
		 "Up N stack frames (numeric arg).")
	(gjdb-def gjdb-down   "down %p"      ">" 
		 "Down N stack frames (numeric arg).")
	(gjdb-def gjdb-print  "print %e"     "\C-p" 
		 "Evaluate expression at point or in region.")
	(gjdb-def gjdb-dump  "dump %e"     nil
		 "Evaluate expression at point or in region.")
	(gjdb-def gjdb-locals "info locals"  nil
		  "Print local variables in selected frame.")
	(gjdb-def gjdb-run   "run"         nil
		  "Run the current application from the beginning.")

	(if gjdb-use-function-keys-p 
	    (progn
	      (local-set-key [f3] 'gjdb-up)
	      (local-set-key [f4] 'gjdb-down)
	      (local-set-key [f5] 'gjdb-step)
	      (local-set-key [f6] 'gjdb-next)
	      (local-set-key [f7] 'gjdb-finish)
	      (local-set-key [f8] 'gjdb-cont)
	      (local-set-key [f9] 'gjdb-print)
	      (local-set-key [\S-f9] 'gjdb-dump)))
	
	(gjdb-menu-item 'quit "Quit" 'gjdb-quit)
	(gjdb-menu-item 'start "Restart Debugger" 'gjdb-restart)
	(gjdb-menu-item 'refresh "Refresh" 'gjdb-refresh)
	(gjdb-menu-item 'eof "Terminal EOF" 'comint-send-eof)
	(gjdb-menu-item 'intr "Interrupt" 'gjdb-intr)
	(gjdb-menu-item 'run  "Run" 'gjdb-run)
	(gjdb-menu-item 'locals "Print Locals" 'gjdb-locals)
	(gjdb-menu-item 'dump  "Print Details" 'gjdb-dump)
	(gjdb-menu-item 'eval  "Print" 'gjdb-print)
	(gjdb-menu-item 'continue "Continue" 'gjdb-cont)
	(gjdb-menu-item 'finish "Finish Function" 'gjdb-finish)
	(gjdb-menu-item 'next "Step Over" 'gjdb-next)
	(gjdb-menu-item 'step "Step Into" 'gjdb-step)
	(gjdb-menu-item 'down "View Callee" 'gjdb-down)
	(gjdb-menu-item 'up "View Caller" 'gjdb-up)
	)
    (use-local-map gjdb-local-map))

  (make-local-variable 'comint-prompt-regexp)
  (setq comint-prompt-regexp "^(.*gjdb [^)\n]*) *")
  (make-local-variable 'paragraph-start)
  (setq paragraph-start comint-prompt-regexp)
  (run-hooks 'gjdb-mode-hook)
  (goto-char (point-max)))

;;;###autoload
(defun gjdb (command-line)
  "Run gjdb using command line COMMAND.  Determine the class, C, being
debugged from COMMAND and start the process in a buffer named *gud-C*.
The current directory becomes the initial working directory
and source-file directory for your debugger."
  (interactive
   (list (read-from-minibuffer "Run gjdb (like this): "
			       (if (consp gjdb-history)
				   (car gjdb-history)
				 (gjdb-default-command "" nil))
			       gjdb-minibuffer-local-map nil
			       '(gjdb-history . 1))))

  (gjdb-init command-line)
  (switch-to-buffer (current-buffer)))

(defun gjdb-from-source ()
  "Run gjdb on program in current buffer, using buffer *gud-FILE*
as for gjdb.  The directory containing FILE becomes the initial 
working directory and source-file directory for your debugger."
  (interactive)
  (let* ((class (gjdb-get-some-class-name))
	 (command-item (assoc class gjdb-source-to-command-map))
	 (command (cdr command-item))
	 (history-tail (member command gjdb-history))
	 (history-posn (if history-tail (- (length gjdb-history) 
					   (length history-tail)
					   -1)
			 1)))
    (setq command
      (read-from-minibuffer "Run gjdb (like this): "
			    (or command (gjdb-default-command class t))
			    gjdb-minibuffer-local-map nil
			    (cons 'gjdb-history history-posn)))
    (if command-item (rplacd command-item command)
      (push (cons class command) gjdb-source-to-command-map))
    (gjdb-init command)
    (switch-to-buffer-other-window (current-buffer))
))
    
(defun gjdb-restart ()
  (interactive)
  (if (string-match "^\\*gud-\\(.*\\)\\*" (buffer-name))
      (let* ((class-name (substring (buffer-name) (match-beginning 1)
				    (match-end 1)))
	     (command (cdr (assoc class-name gjdb-source-to-command-map))))
	(if command
	    (gjdb command)
	  (gjdb (gjdb-default-command class-name nil))))
    (call-interactively 'gjdb)))	  
	    
(defun gjdb-quit ()
  "End gjdb session, and bury buffer."
  (interactive)
  (if (and (get-buffer-process (current-buffer))
	   (y-or-n-p "Terminate current session first? "))
      (gjdb-clean-buffer-process))
  (bury-buffer))

(defun gjdb-clean-buffer-process (&optional buffer0) 
  (let* ((process (get-buffer-process (or buffer0 (current-buffer)))))
    (if process
	(progn
	  (set-process-filter process t)
	  (condition-case x 
	      (delete-process process)
	    (error nil))))))

(defun gjdb-default-command (class set-classpath-p)
"A default command to start GJDB debugging the class named CLASS.  If 
SET-CLASSPATH-P, include a heuristically chosen -classpath directive, if 
needed, on the assumption that the class we are loading is in the current
directory, but the base of the class hierarchy may be a parent directory."
  (let (command)
    (setq command (concat gjdb-program " "))
    (if (and set-classpath-p (string-match "\\." class))
	(let (dir k)
	  (setq dir "" k -1)
	  (while (setq k (string-match "\\." class (1+ k)))
	    (setq dir (concat dir "/..")))
	  (setq dir (substring dir 1))
	  (if (getenv "CLASSPATH")
	      (setq dir (concat (getenv "CLASSPATH") ":" dir)))
	  (setq command (concat command "-classpath " dir " "))))
    (concat command class)))

(add-hook 'java-mode-hook 'gjdb-set-local-sourcefile-map)

(provide 'gjdb)
