(ns leiningen.clojuredocs
  (:require [clojure.java.io :as io]
            [clojure.tools.namespace :as clj-ns]
            [clojure.pprint :as pp]))

(defn munge-doc
  "Take a map of a clojure symbol, and munge it into an indexable doc map."
  [doc]
  (-> doc
      (update-in [:ns] str)
      (update-in [:name] str)
      (update-in [:tag] pr-str)
      (update-in [:arglists] (fn [arglists] (map str arglists)))))

(defn get-project-meta
  "Return a map of information about the project that should be indexed."
  [project]
  (select-keys project [:name :group :url :description :version :group]))

(defn serialize-project-info
  "TODO: Write the docs to a file"
  [info]
  (pp/pprint info)
  (flush))

(defn read-namespace
  "Reads a file, serializing docs to a file for import to ClojureDocs"
  [f]
  (let [ns-dec (clj-ns/read-file-ns-decl f)
        ns-name (second ns-dec)]
    (printf "[+] Processing %s...\n" (or ns-name f))
    (flush)
    (try
      (require ns-name)
      (catch Exception e
        (println "Error requiring" ns-name e)))
    {(str ns-name) (->> ns-name
                        ns-interns
                        vals
                        (map meta)
                        (map munge-doc))}))

;; actual lein plugin function
(defn clojuredocs
  "Publish vars for clojuredocs"
  [project]
  (let [paths (or (:source-paths project) [(:source-path project)])
        source-files (mapcat #(-> %
                                  io/file
                                  clj-ns/find-clojure-sources-in-dir)
                             paths)
        proj-meta (get-project-meta project)
        data-map (merge proj-meta
                        {:namespaces
                         (apply merge (for [source-file source-files]
                                        (read-namespace source-file)))})]
    (serialize-project-info data-map)))
