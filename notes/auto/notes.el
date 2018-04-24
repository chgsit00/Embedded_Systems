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
    "sec:orgf5e4737"
    "sec:orgef98480"
    "sec:org9600a05"
    "sec:org7e5fafa"
    "sec:orgc97a297"
    "sec:org03748f5"
    "sec:org0663f3c"
    "sec:orgcb80fd3"
    "sec:org319f9aa"
    "sec:org6b348b8"
    "sec:orgcdc663b"
    "sec:orgd6417a8"
    "sec:org020604d"
    "sec:orgd17f5ad"
    "sec:org9ae6af9"
    "sec:orgf5ddf22"
    "sec:orgb0029c3"
    "sec:org4839a4f"
    "sec:org83012f6"
    "sec:orgaa87f52"
    "sec:org3104bbe"
    "sec:orgb9bf40c"
    "sec:org01bd020"
    "sec:orgcaf13d7"
    "sec:orgf77b6e7"
    "sec:org6cdb27c"))
 :latex)

