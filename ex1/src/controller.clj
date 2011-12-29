(ns controller
  (:use input rover plateau utils))

(defn process-rover [plateau [coordinates moves]]
  (let [rover (apply new-rover coordinates)]    
    (println "Processing rover:" rover)
    (loop [moves moves old-position rover plateau (position-rover plateau rover)]
      (if (empty? moves)
        plateau
        (let [positioned (calculate-position old-position (first moves) plateau)]          
          (recur (rest moves) positioned (move-rover old-position positioned (collision-checked positioned plateau))))))))

(defn process-moves [plateau rovers]
  (reduce process-rover plateau rovers))

(defn process-input [filename]
  (let [[[x y] rovers] (load-input filename)]
    (-> (new-plateau x y)
        (process-moves rovers)
        (report-rovers))))

