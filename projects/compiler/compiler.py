#!/usr/bin/env python

import os

target  = "output"
options = "-f elf32"

class SourceREPL(object):
	def __init__(self):
		pass
	def __iter__(self):
		return self
	def next (self):
		line = raw_input("> ")
		if line == "exit":
			raise StopIteration
		else:
			return line

source = SourceREPL()

asm_data = """
msg	        db    'Hello, world!',0xa   ;our dear string
len	        equ   $ - msg               ;length of our dear string
"""

class Compiler(object):
	def __init__ (self, outfile, source):
		self.var_id = 0
		self.data_section = ""
		self.outfile = outfile
		self.source  = source
		self.prologue = """
section	.text
    global _start   ;must be declared for linker (ld)

_start:             ;tell linker entry point
;    Program code   ;;;;;
"""
		self.runtime = """
;    Runtime code   ;;;;;
__builtin__print:
    ; Stack: T [addr of string, length of string] B
    ;push ebp 
    ;mov ebp, esp
	mov     ecx, [esp + 8]  ; string to write
    mov     edx, [esp + 4]  ; string length
    mov     ebx, 1
    mov     eax, 4
    int     0x80
    ;mov esp, ebp
    ;pop ebp
    ret

__builtin__strlen:
	xor     eax, eax
	mov     edx, [esp + 4]
  .loop1:
	mov     bx, [edx]
	or      bx, 0
	jz      .end
	inc     eax
	inc     edx
	jmp     .loop1
  .end:
	ret

__builtin__exit:
    mov     eax,1           ; system call number (sys_exit)
    int     0x80            ; call kernel
"""

	def new_var (self, type, value):
		names = []
		if type == "string":
			self.data_section += '__var__%d db "%s", 0x0\n' % (self.var_id, value)
			self.data_section += '__var__%d equ $ - __var__%d\n' % (self.var_id+1, self.var_id)
			names.append("__var__%d" % (self.var_id,))
			names.append("__var__%d" % (self.var_id+1,))
			self.var_id += 2
		return names
	
	def compile_source(self):
		data = ""
		code_stack = []
		stack = []
		token = ""
		quote = False
		self.var_id = 0
		for line in self.source:
			san = ""
			for c in line:
				if c == '"':
					quote = not quote
				elif quote:
					san += c
				else:
					if c not in " \t\n()":
						san += c
					elif c == "(":
						pass
					else:
						stack.append(san)
						san = ""
						if c == ')':
							if stack[0] == "print":
								vars = self.new_var("string", stack[1])
								self.outfile.write("    mov eax, " + vars[0] + "\n")
								self.outfile.write("    push eax\n")
								self.outfile.write("    call __builtin__strlen\n")
								self.outfile.write("    push eax\n")
								self.outfile.write("    call __builtin__print\n")
								self.outfile.write("    add esp, 8 ; Clean stack\n")
							stack = []
		return data
				
	def compile(self):
		self.data_section = ""
		self.outfile.write(self.prologue)
		self.compile_source()
		self.outfile.write("    call __builtin__exit")
		self.outfile.write(self.runtime)
		self.outfile.write("""
section	.data
;    Data           ;;;;;
""")
		self.outfile.write(self.data_section)

with open(target + ".asm", "w") as t:
	compiler = Compiler(t, source)
	compiler.compile()

os.system("nasm %s %s.asm" % (options, target))
os.system("ld -o %s %s.o" % (target, target))
os.system("rm %s.o" % (target,))
#os.system("rm %s.asm" % (target,))
