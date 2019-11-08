# SVGraph

Sample output

```
Initial genome:
ACGTACGTACGTACGTACGT

Processing insertion in testChr at position 1 of sequence GGGG
  Removing edge from 1+ to 2- with sequence ""
  Adding edge from 1+ to 2- with sequence "GGGG"
Processing deletion in testChr at position 16 of length 2
  Removing edge from 16+ to 17- with sequence ""
  Removing edge from 18+ to 19- with sequence ""
  Adding edge from 16+ to 19- with sequence ""
Processing inversion signature in testChr from position 2 to 5 with strand ++
  Adding edge from 2+ to 5+ with sequence ""
  Removing edge from 5+ to 6- with sequence ""
  Removing edge from 2+ to 3- with sequence ""
Processing inversion signature in testChr from position 3 to 6 with strand --
  Adding edge from 3- to 6- with sequence ""
  Removing edge from 2+ to 3- with sequence ""
    Edge already removed so doing nothing
  Removing edge from 5+ to 6- with sequence ""
    Edge already removed so doing nothing
Processing duplication in testChr at position 10 of sequence 
  Adding edge from 10- to 15+ with sequence ""

Final genome:
AGGGGCATGCGTACGTACGCGTACGTGT
```
