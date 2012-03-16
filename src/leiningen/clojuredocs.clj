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
  [{:keys [name group url description version] :as project}]
  {:project (str (if (= name group)
                   ""
                   (str group "/"))
                 name)
   :url url
   :version version
   :descripton description})

(defn serialize-docs
  "TODO: Write the docs to a file"
  [proj-meta docs]
  #_(pp/print-table [:ns :name :arglists :private :dynamic] docs)
  (pp/pprint (assoc proj-meta :vars (vec docs))))

(defn read-file
  "Reads a file, serializing docs to a file for import to ClojureDocs"
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
          docs (->> metas
                    (map (fn [m] (assoc m :project (:project proj-meta))))
                    (map munge-doc))]
      (serialize-docs proj-meta docs))))


;; testing vars
(def ^{:private true :doc "a test variable"} test-var 42)

(defn ^Long typed-fn [] 5)

(defn- ^:dynamic test-fn
  "A function to test read-file against"
  ([x]
     (println x))
  ([x y]
     (println x y)))

;; actual lein function
(defn clojuredocs
  "Publish vars for clojuredocs"
  [project]
  (let [paths (or (:source-paths project) [(:source-path project)])
        source-files (mapcat #(-> % io/file clj-ns/find-clojure-sources-in-dir)
                             paths)]
    (doseq [source-file source-files]
      (read-file project source-file))
    (flush)))
