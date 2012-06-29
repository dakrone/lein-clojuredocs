(ns clojure.analyzer
  (:require [clojure.java.io :refer [file]]
            [clojure.tools.namespace :as clj-ns]
            [leiningen.clojuredocs :as cd]))

(def clojure-project
  {:name "clojure"
   :group "org.clojure"
   :url "http://clojure.org"
   :description "Clojure core environment and runtime"
   :scm "http://github.com/clojure/clojure"
   :license {:name "Eclipse Public License"
             :url "http://www.eclipse.org/legal/epl-v10.html"}
   :version (clojure-version)})

(def blacklist
  #{"core_deftype.clj"
    "core_print.clj"
    "core_proxy.clj"
    "genclass.clj"
    "gvec.clj"
    "cl_format.clj"
    "column_writer.clj"
    "dispatch.clj"
    "pprint_base.clj"
    "pretty_writer.clj"
    "print_table.clj"
    "utilities.clj"
    "java.clj"})

(defn gen-clojure [clojure-dir]
  (let [clj-src-dir (file clojure-dir "src" "clj")
        clj-files (clj-ns/find-clojure-sources-in-dir clj-src-dir)
        clj-files (remove #(contains? blacklist (.getName %)) clj-files)
        data-map (cd/generate-all-data clojure-project clj-files)]
    (cd/serialize-project-info data-map)
    (println "[=] Done.")))

;; How to use this to generate clojure/doc data:
#_
(gen-clojure "/Users/hinmanm/src/clojure")
