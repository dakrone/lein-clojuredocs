(ns leiningen.clojuredocs
  (:require [leiningen.core.eval :as lein]))

;; actual lein plugin function
(defn clojuredocs
  "Publish vars for clojuredocs"
  [project]
  (lein/eval-in-project
   (-> project
       (update-in [:dependencies] conj ['cadastre "0.1.1"]))
   `(binding [cadastre.analyzer/*verbose* true]
      (cadastre.analyzer/gen-project-docs-json '~project))
   '(require 'cadastre.analyzer)))
