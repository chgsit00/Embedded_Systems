(TeX-add-style-hook
 "notes"
 (lambda ()
   (add-to-list 'LaTeX-verbatim-environments-local "lstlisting")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "path")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "url")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "lstinline")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "nolinkurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperbaseurl")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperimage")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "hyperref")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "path")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "lstinline")
   (TeX-run-style-hooks
    "/Users/dennismuller/dotfiles/networkAssignmentConfig")
   (LaTeX-add-labels
    "sec:org9d46027"
    "sec:orgb55156f"
    "sec:org697384d"
    "sec:org47f91d8"
    "sec:orge38cbbc"
    "sec:org3a095f8"
    "sec:org7d640f8"
    "sec:org320397e"
    "sec:org511a6a2"
    "sec:org505b32f"
    "sec:org1401e45"
    "sec:orge1bedac"
    "sec:org77e7451"
    "sec:orgc960a3e"
    "sec:orgeb9e4a8"
    "sec:orgdc7d373"))
 :latex)

