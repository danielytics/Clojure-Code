
section	.text
    global _start   ;must be declared for linker (ld)

_start:             ;tell linker entry point
;    Program code   ;;;;;
    mov eax, __var__0
    push eax
    call __builtin__strlen
    push eax
    call __builtin__print
    add esp, 8 ; Clean stack
    mov eax, __var__2
    push eax
    call __builtin__strlen
    push eax
    call __builtin__print
    add esp, 8 ; Clean stack
    call __builtin__exit
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

section	.data
;    Data           ;;;;;
__var__0 db "A", 0x0
__var__1 equ $ - __var__0
__var__2 db "B", 0x0
__var__3 equ $ - __var__2
