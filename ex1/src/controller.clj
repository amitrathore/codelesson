(ns controller
  (:use input rover plateau))

(defn process-rover [plateau [rover moves]]
  (loop [moves moves old-position rover plateau (position-rover plateau rover)]
    (if (empty? moves) 
      plateau
      (let [new-position (calculate-position old-position (first moves))]
        (recur (rest moves) new-position (move-rover old-position new-position plateau))))))

(defn process-moves [plateau rovers]
  (reduce process-rover plateau rovers))

(defn process-input [filename]
  (let [[[x y] rovers] (load-input filename)]
    (-> (new-plateau x y)
        (process-moves rovers)
        (report-rovers))))

