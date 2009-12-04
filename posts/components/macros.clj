(defmacro program [& components]
  `(run-components ~@components))
 
(defmacro component [name & handlers]
  `(make-component ~name (conj {} ~@handlers)))

(defmacro on [handler args & body]
  {handler `(fn ~args ~@body)})

