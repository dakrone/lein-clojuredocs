(ns leiningen.clojuredocs
  (:require [clojuredocs.analyzer]
            [leiningen.core.eval :as lein]))

;; actual lein plugin function
(defn clojuredocs
  "Publish vars for clojuredocs"
  [project]
  (lein/eval-in-project
   (update-in project [:dependencies] conj ['lein-clojuredocs "1.0.0-SNAPSHOT"])
   `(clojuredocs.analyzer/gen-project-docs '~project)
   '(require 'clojuredocs.analyzer)))
