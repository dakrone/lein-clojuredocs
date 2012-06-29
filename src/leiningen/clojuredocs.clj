(ns leiningen.clojuredocs
  (:require [clojure.java.io :as io]
            [clojure.tools.namespace :as clj-ns]
            [clojure.pprint :as pp]
            [cheshire.core :as json])
  (:import (java.io FileOutputStream OutputStreamWriter)
           (java.util.zip GZIPOutputStream)))

(defn munge-doc
  "Take a map of a clojure symbol, and munge it into an indexable doc map."
  [doc]
  (-> doc
      (dissoc :protocol :inline)
      (update-in [:ns] str)
      (update-in [:name] str)
      (update-in [:tag] #(when % (pr-str %)))
      (update-in [:arglists] (fn [arglists] (map str arglists)))))

(defn get-project-meta
  "Return a map of information about the project that should be indexed."
  [project]
  (select-keys project [:name :url :description :version :group]))

(defn serialize-project-info
  "Writes json-encoded project information to a gzipped file."
  [info]
  (let [filename (str (:name info) "-" (:version info) ".json.gz")]
    (println "[-] Writing output to" filename)
    (with-open [fos (FileOutputStream. filename)
                gzs (GZIPOutputStream. fos)
                os (OutputStreamWriter. gzs)]
      (.write os (json/encode info)))))

(defn read-namespace
  "Reads a file, returning a map of the namespace to a vector of maps with
  information about each var in the namespace."
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
    (serialize-project-info data-map)
    (println "[=] Done.")))
