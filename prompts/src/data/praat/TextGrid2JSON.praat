form TextGrid2JSON
  sentence TextGrid_file ../resources/alexander.TextGrid
endform

Read from file... 'textGrid_file$'
t = 1
ni = Get number of intervals... t
p = 0
printline [
for i to ni
  text$ = Get label of interval... t i
  if length(text$)
    p += 1
    p$ = "'p'"    
    p$ = if p < 10 then "0'p$'" else p$ fi
    p$ = if p < 100 then "0'p$'" else p$ fi
    start = Get starting point... t i
    end = Get end point... t i
    if p > 1
      printline ,
    endif
    printline {
    printline   "prompt": "prompt_'p$'",
    printline   "text": "'text$'",
    printline   "start": 'start',
    printline   "end": 'end'
    print }
  endif
endfor
printline ]

