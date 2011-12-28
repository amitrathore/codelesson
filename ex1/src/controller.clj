(ns controller
  (:use input rover plateau))

(defn process-rover [plateau [coordinates moves]]
  (let [new-rover (apply new-rover coordinates)]
    (loop [moves moves old-position new-rover plateau (position-rover plateau new-rover)]
      (if (empty? moves)
        plateau
        (let [new-position (calculate-position old-position (first moves))]
          (recur (rest moves) new-position (move-rover old-position new-position plateau)))))))

(defn process-moves [plateau rovers]
  (reduce process-rover plateau rovers))

(defn process-input [filename]
  (let [[[x y] rovers] (load-input filename)]
    (-> (new-plateau x y)
        (process-moves rovers)
        (report-rovers))))

