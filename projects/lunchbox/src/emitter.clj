(ns emitter)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; EMIT ASSEMBLY CODE
;

(defmulti emit (fn [a & b] a))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defstruct operation :opcode :operands)

(defn oper [op]
  (struct operation op []))

(defn unop [op a]
  (struct operation op [a]))

(defn binop [op a b]
  (struct operation op [a b]))

(defn trinop [op a b c]
  (struct operation op [a b c]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmethod emit '+ [_ a b]
  (list
    (binop 'add a b))i)

(defmethod emit '- [_ a b]
  (list
    (binop 'sub a b)))

(defmethod emit '+1 [_ a]
  (list
    (unop 'inc a)))

(defmethod emit '-1 [_ a]
  (list
    (unop 'dec a)))

(defmethod emit 'push [_ a]
  (list
    (unop 'push a)))

(defmethod emit 'pop [_ a]
  (list
    (unop 'pop a)))

(defmethod emit '= [_ a b]
  (list
    (binop 'mov a b)))

(defmethod emit 'func [_ a]
  (list
    (unop :raw (str "__function__" a ":"))))

(defmethod emit 'return [_]
  (list
    (oper 'ret)))

(defmethod emit 'start [_]
  (list
    (unop :raw (str "
section .text
    global _start
_start:
    mov     esi, [__builtin__callstack]"))))

(defmethod emit 'end [_]
  (list
    (unop :raw (str "
    ; Terminate
    xor     eax, eax         ; System call (sys_exit)
    int     0x80             ; Call kernel

__builtin__callstack resb 1024"))))

(defmethod emit 'label [_ a]
  (list
    (unop :label (str "__label__" a))))

(defmethod emit 'jump-if-true [_ a b]
  (list
    (unop 'cmp a a)
    (unop 'jz  (str "__label__" b))))

(defmethod emit 'jump-if-false [_ a b]
  (list
    (binop 'cmp a a)
    (unop ' jnz (str "__label__" b))))

(defmethod emit 'jump-if-equal [_ a b c]
  (list
    (binop 'cmp a b)
    (unop  'je  (str "__label__" c))))

(defmethod emit 'jump-if-notequal [_ a b c]
  (list
    (binop 'cmp a b)
    (unop  'jne (str "__label__" a))))

(defmethod emit 'jump-if-> [_ a b c]
  (list
    (binop 'cmp a b)
    (unop  'jg  (str "__label__" c))))

(defmethod emit 'jump-if->= [_ a b c]
  (list
    (binop 'cmp a b)
    (unop  'jge (str "__label__" c))))

(defmethod emit 'jump-if-< [_ a b c]
  (list
    (binop 'cmp a b)
    (unop  'jl  (str "__label__" c))))

(defmethod emit 'jump-if-<= [_ a b c]
  (list
    (binop 'cmp a b)
    (unop  'jle (str "__label__" c))))

(defmethod emit 'and [_ a b]
  (list
    (binop 'and a b)))

(defmethod emit 'or [_ a b]
  (list
    (binop 'or a b)))

(defmethod emit 'xor [_ a b]
  (list
    (binop 'xor a b)))

(defmethod emit 'not [_ a]
  (list
    (unop 'not a)))

(defmethod emit 'equal [_ a b]
  (list
    (binop 'and a b)))

(defmethod emit 'compare [_ a b]
  (list
    (binop 'cmp a b)))


