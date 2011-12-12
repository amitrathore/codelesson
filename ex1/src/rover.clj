(ns rover
  (:use plateau))

(def LEFT {\N \W \W \S \S \E \E \N})

(def RIGHT {\N \E \E \S \S \W \W \N})

(defn new-rover [x y bearing]
  [x y bearing])

(defn- moved-rover [x y bearing]
  (condp = bearing
    \N (new-rover x (inc y) bearing)
    \E (new-rover (inc x) y bearing)
    \W (new-rover (dec x) y bearing)
    \S (new-rover x (dec y) bearing)))

(defn calculate-position [[x y bearing :as rover] move]
  (condp = move
    \L (new-rover x y (LEFT bearing)) 
    \R (new-rover x y (RIGHT bearing))
    \M (moved-rover x y bearing)))

