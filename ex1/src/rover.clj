(ns rover
  (:use plateau))

(def LEFT {\N \W \W \S \S \E \E \N})

(def RIGHT {\N \E \E \S \S \W \W \N})

(let [id-gen (atom 0)]
  (defn next-id []
    (swap! id-gen inc)))

(defn new-rover 
  ([x y bearing]
     (new-rover (next-id) x y bearing))
  ([id x y bearing]
     {:id id :x x :y y :bearing bearing}))

(defn- moved-rover [{:keys [id x y bearing]}]
  (condp = bearing
    \N (new-rover id x (inc y) bearing)
    \E (new-rover id (inc x) y bearing)
    \W (new-rover id (dec x) y bearing)
    \S (new-rover id x (dec y) bearing)))

(defn calculate-position [{:keys [id x y bearing] :as rover} move]
  (condp = move
    \L (new-rover id x y (LEFT bearing)) 
    \R (new-rover id x y (RIGHT bearing))
    \M (moved-rover rover)))

