form TextGrid2JSON
  sentence TextGrid_file ../resources/alexander.TextGrid
  sentence JSON_file ../resources/alexander.json
endform

Read from file... 'textGrid_file$'
t = 1
lt = 2
ni = Get number of intervals... t
p = 0
filedelete 'jSON_file$'
fileappend 'jSON_file$' ['newline$'
for i to ni
  text$ = Get label of interval... t i
  if length(text$)
    p += 1
    p$ = "'p'"    
    p$ = if p < 10 then "0'p$'" else p$ fi
    p$ = if p < 100 then "0'p$'" else p$ fi
    start = Get starting point... t i
    end = Get end point... t i
    l = Get interval at time... lt (start + end) / 2
    l$ = Get label of interval... lt l
    if p > 1
      fileappend 'jSON_file$' ,'newline$'
    endif
    fileappend 'jSON_file$' {'newline$'
    fileappend 'jSON_file$'   "prompt": "'l$'_'p$'",'newline$'
    fileappend 'jSON_file$'   "text": "'text$'",'newline$'
    fileappend 'jSON_file$'   "start": 'start','newline$'
    fileappend 'jSON_file$'   "end": 'end''newline$'
    fileappend 'jSON_file$' }
  endif
endfor
fileappend 'jSON_file$' 'newline$']'newline$'
