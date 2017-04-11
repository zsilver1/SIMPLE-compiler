Usage:

"./sc" ["-" ("s"|"c"|"t"|"a"|"i")] [filename]


Compiler for the SIMPLE programming language, written in Java. In order to compile a SIMPLE program to ARM assembly, 
first run "make" and then "./sc [filename]". In order to interpret a SIMPLE program, instead run "./sc -i [filename]". The other
options display the various intermediate stages of the compiler, such as the symbol table ("-t") or the abstract syntax tree ("-a").

Example SIMPLE program that endlessly generates random numbers:
```
(* $Id: random.sim 30 2006-02-17 17:39:34Z phf $ *)

PROGRAM Random;
CONST
  a = 16807;
  m = 2147483647;
  q = m DIV a;
  r = m MOD a;
VAR
  Z: INTEGER; (* seed *)
  g: INTEGER; (* temporary *)
BEGIN
  Z := 1;
  (* Forever... *)
  WHILE 0 = 0 DO
    (* ...make next random number... *)
    g := a*(Z MOD q) - r*(Z DIV q);
    IF g > 0 THEN Z := g ELSE Z := g + m END;
    (* ...and print it. *)
    WRITE Z
  END
END Random.

(*
  This is an integer version of the random number generator
  described in Wirth, Reiser: Programming in Oberon, 1992.
  I guess the original reference is Park and Miller, 1988.
*)
```
