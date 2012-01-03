(ns patterns)

(defn single? [x]
  (or (nil? x) (symbol? x)))

(defn matches? [pattern data]
  (cond 
   (= '? pattern) true
   (and (single? pattern) (single? data)) (= pattern data)
   (or (single? pattern) (single? data)) false
   (and (empty? pattern) (empty? data)) true
   :default (and (matches? (first pattern) (first data))
                 (matches? (rest pattern) (rest data)))))