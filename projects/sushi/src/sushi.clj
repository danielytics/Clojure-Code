(ns sushi
  (:import org.lwjgl.Sys
           org.lwjgl.input.Keyboard
           [org.lwjgl.opengl Display GL11]))

(defmacro in-thread [thread-name & body]
  "Evaluate body in its own thread"
  `(.start (Thread. (fn [] ~@body) ~thread-name)))

(defmacro async-loop [thread-name & body]
  "Asynchronously loop body until it evaluates to false"
  `(in-thread ~thread-name
     (loop [running# true]
       (if (not running#)
         nil
         (recur (do ~@body))))))

(defmacro push-matrix [& code]
  "War body between a glPushMatrix/glPopMatrix pair"
  `(do
     (GL11/glPushMatrix)
     ~@code
     (GL11/glPopMatrix)))

(defmacro gl-begin [what & code]
  "Wrap body between a glBegin/glEnd pair"
  `(do
     (GL11/glBegin ~what)
     ~@code
     (GL11/glEnd))
)

(defn render-func [state]
  (GL11/glClear (or GL11/GL_COLOR_BUFFER_BIT GL11/GL_STENCIL_BUFFER_BIT))
  ; center square according to screen size
  (push-matrix
    (let [dm (Display/getDisplayMode)]
      (GL11/glTranslatef (/ (. dm getWidth) 2)
                         (/ (. dm getHeight) 2)
                         0.0))
    ; rotate square according to angle
    (GL11/glRotatef (:angle state) 0 0 1.0)
    ; render the square
    (gl-begin GL11/GL_QUADS
      (GL11/glVertex2i -50 -50)
      (GL11/glVertex2i  50 -50)
      (GL11/glVertex2i  50  50)
      (GL11/glVertex2i -50  50))))

(defn init-ortho []
  "Set up orthographic mode with a 1:1 pixel ratio"
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (let [dm (Display/getDisplayMode)]
    (GL11/glLoadIdentity)
    (GL11/glOrtho 0.0 (. dm getWidth) 0.0 (. dm getHeight) -1.0 1.0)
    (GL11/glMatrixMode GL11/GL_MODELVIEW)
    (GL11/glLoadIdentity)
    (GL11/glViewport 0 0 (. dm getWidth) (. dm getHeight))))

(defn change [state field value]
  "Atomically change a single field in the mutable state"
  (swap! state conj {field value}))

(defn run-game [title
                new-state
                logic render
                input-map]
  (Display/setTitle title)
  (Display/setFullscreen false)
  (Display/setVSyncEnabled true)
  ; Create default display of 640x480
  (Display/create)

  ; Put the window into orthographic projection mode with 1:1 pixel ratio.
  (init-ortho)

  (let [state (atom (new-state))]
    ; Update logic loop
    (async-loop "Sushi:Logic"
      (swap! state conj (logic @state))
      (Thread/sleep 1)
      (:running @state))

    ; Update input loop
    (async-loop "Sushi:Input"
      (if (Display/isCloseRequested)
        (change state :running false)
        (dorun
          (map (fn [item]
                 (if (Keyboard/isKeyDown (first item))
                   (change state
                           (second item)
                           ((last item) @state))))
               input-map)))
      (Thread/sleep 1)
      (:running @state))

    ; Update display loop
    (.setName (Thread/currentThread) "Sushi:Renderer")
    (while (:running @state)
      (Display/update)
      (if (Display/isActive)
        (do
          (render @state)
          (Display/sync 60))
        (do
          (Thread/sleep 100)
          (when (or (Display/isVisible)
                    (Display/isDirty))
            (render @state))))
      (:running @state))

    ; Terminate
    (Display/destroy)))

