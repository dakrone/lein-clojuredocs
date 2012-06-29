(ns leiningen.clojuredocs
  (:require [clojuredocs.analyzer :as analyzer]))

;; actual lein plugin function
(defn clojuredocs
  "Publish vars for clojuredocs"
  [project]
  (analyzer/gen-project-docs project))
