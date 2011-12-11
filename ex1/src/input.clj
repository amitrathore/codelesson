(ns input
  (:require (clojure.contrib [duck-streams :as ducky])))

(def integer #(Integer/parseInt %))

(defn- tokenize-size [line]
  (map integer (.split line " ")))

(defn tokenize-position [position]
  (let [[x y bearing] (.split position " ")]
    [(integer x) (integer y) (first bearing)]))

(defn tokenize-rover [[position moves]]
  [(tokenize-position position) moves])

(defn- tokenize-rovers [lines]
  (->> lines
       (partition 2)
       (map tokenize-rover)))

(defn load-input [filename]
  (let [lines (ducky/read-lines filename)]
    [(tokenize-size (first lines)) (tokenize-rovers (rest lines))]))

