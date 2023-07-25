(ns vanity.main
  (:require
   [clojure.edn :as edn]
   [clojure.java.shell :as sh]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]))

(defn read-aliases
  [path]
  (into [] (-> path slurp edn/read-string :aliases keys)))

(defn some-random-shuffle
  ([aliases]
   (some-random-shuffle aliases 0.5))
  ([aliases prob]
   (->> #(random-sample prob aliases)
        repeatedly
        (filter seq)
        first)))

(defn prepare-cmd
  [aliases]
  {:pre [(seq aliases)]}
  ["clojure" "-P" (format "-A%s" (str/join "" aliases))])

(def ^:private separator
  (str (str/join "" (repeat 80 "-")) "\n"))

(defn- maybe-log
  [msg s]
  (when-not (str/blank? s)
    (println (str/join "" [separator msg "\n" s separator]))))

(defn -main
  [path]
  (let [aliases (-> path read-aliases some-random-shuffle)
        cmd     (prepare-cmd aliases)
        result  (apply sh/sh cmd)]
    (pprint {:aliases aliases
             :cmd     cmd})
    (maybe-log "stdout" (:out result))
    (maybe-log "stderr" (:err result))
    (System/exit (:exit result))))

(comment
  (apply sh/sh
         (prepare-cmd
          (-> "deps.edn" read-aliases some-random-shuffle))))
