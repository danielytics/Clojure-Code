(
[:function
[increment
    [:push 1]
    [:binop +]
]]

[:function
[main
    [:push 10]
    [:call increment]
    [:stack dup]
    [:stack dup]
    [:push 4]
    [:call increment]
    [:push 5]
    [:push 1]
    [:call increment]
    [:binop +]
    [:call increment] 
    [:binop -]
    [:binop -]
    [:binop -]
    [:binop +]
    [:flush]
]]
)
