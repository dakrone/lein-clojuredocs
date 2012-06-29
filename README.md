# lein-clojuredocs

lein-clojuredocs gives you a way to generate data that can be indexed
for a new clojuredocs project. It's still heavily a work in progress,
but I'd appreciate any bug reports when running this against your own
project.

## Usage

In ~/.lein/profiles.clj:

```clojure
{:user {:plugins [[lein-clojuredocs "1.0.0"]]}}
```

Then in a project:

```
% lein2 clojuredocs
```

Example of creating a project.clj file for one of the Clojure 1.3
contrib libraries, java.jdbc.

* Go to https://github.com/clojure, find java.jdbc in the list of git
  repositories.

* Find its Git read-only URL git://github.com/clojure/java.jdbc.git
  (the other choices should work, too).

* Create a local copy of the repository and look at where of the
  Clojure source files are within it:

```
% git clone git://github.com/clojure/java.jdbc.git
% cd java.jdbc
% find . -name '*.clj'
./src/main/clojure/clojure/java/jdbc/internal.clj
./src/main/clojure/clojure/java/jdbc.clj
./src/test/clojure/clojure/java/test_jdbc.clj
./src/test/clojure/clojure/java/test_utilities.clj
```

* The "main" source file is src/main/clojure/clojure/java/jdbc.clj.
  Look at the ns declaration within to see that it declares the
  namespace clojure.java.jdbc.  Note that the path src/main/clojure
  comes before clojure/java/jdbc.clj in the source file.  We must use
  this in the Leiningen 2 project file.

* Create a file project.clj in the java.jdbc directory and fill it
  with:

```clojure
;; version number copied from pom.xml, the version tag associated with
;; the artifactId tag with the value java.jdbc.
(defproject clojure.java.jdbc "0.1.4-SNAPSHOT"
  ;; TBD: Is the description text important for lein-clojuredocs?  I
  ;; just copied this from the first line of description in README.md.
  :description "A Clojure wrapper for JDBC-based access to databases"
  ;; The following line is necessary for lein-clojuredocs to work.
  :eval-in :leiningen
  ;; Here is where we put the src/main/clojure partial path mentioned
  ;; above.
  :source-paths [ "src/main/clojure" ]
  ;; java.jdbc only needs clojure 1.3.0 as a dependency.  Other
  ;; contrib modules may need more.
  :dependencies [[org.clojure/clojure "1.3.0"]])
```

* Now run `lein clojuredocs` in the java.jdbc project root directory:

```
% lein2 clojuredocs
[+] Processing clojure.java.jdbc...
[+] Processing clojure.java.jdbc.internal...
[-] Writing output to clojure.java.jdbc-0.1.4-SNAPSHOT.json.gz
[=] Done.
```


## License

Copyright (C) 2012 Lee Hinman

Distributed under the Eclipse Public License, the same as Clojure.
