(ns leiningen.clojuredocs
  (:require [clojure.java.io :as io]
            [clojure.tools.namespace :as clj-ns])
  (:use [doric.core :only [table]]))

(defn munge-doc
  "Take a map of a clojure symbol, and munge it into an indexable doc map."
  [doc]
  (-> doc
      (update-in [:ns] str)))

(defn get-project-meta
  "Return a map of information about the project that should be indexed."
  [project]
  {:project (str (if (= (:name project) (:group project))
                   ""
                   (str (:group project) "/"))
                 (:name project))
   :version (:version project)
   :description (:description project)
   :url (:url project)})

(defn serialize-docs
  "TODO: Write the docs to a file"
  [docs]
  #_(pp/print-table docs)
  (pp/pprint docs))

(defn read-file
  "Reads a file"
  [project f]
  (let [ns-dec (clj-ns/read-file-ns-decl f)
        ns-name (second ns-dec)
        proj-meta (get-project-meta project)]
    (printf "[+] Processing %s...\n" (or ns-name f))
    (flush)
    (try
      (require ns-name)
      (catch Exception e
        (println "Error requiring" ns-name e)))
    (let [vars (vals (ns-interns ns-name))
          metas (map meta vars)
          docs (map munge-doc (map #(merge proj-meta %) metas))]
      (serialize-docs docs))))


;; testing vars
(def ^{:dynamic true :private true :doc "a test variable"} test-var 42)

(defn- ^:dynamic test-fn
  "A function to test read-file against"
  ([x]
     (println x))
  ([x y]
     (println x y)))

(defn clojuredocs
  "Publish vars for clojuredocs"
  [project]
  (let [paths (or (:source-paths project) [(:source-path project)])
        source-files (mapcat #(-> % io/file clj-ns/find-clojure-sources-in-dir)
                             paths)]
    (doseq [source-file source-files]
      (cd/read-file project source-file))
    (flush)))
