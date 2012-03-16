(ns foo.bar
  "A test namespace.")

;; testing vars
(def ^{:private true :doc "a test variable"} test-var 42)

(defn ^Long typed-fn [] (constantly 5))

(defn- ^:dynamic test-fn
  "A function to test read-file against"
  ([x]
     (println x))
  ([x y]
     (println x y)))

