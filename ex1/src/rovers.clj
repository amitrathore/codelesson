(ns rovers)

(defn update-grid [x y grid move]
  (condp = move
    \L (assoc grid )))

(defn process-rover [[max-x max-y grid] [[x y bearing :as rover] moves]]
  (let [plateau-with-rover (assoc-in grid [x y] rover)]
    (doseq [m moves]
      ())
    [max-x max-y (reduce update-grid plateau-with-rover moves)]))

(defn process-moves [plateau rovers]
  (reduce process-rover plateau rovers))

(defn new-plateau [[x y]]
  [x y {}])

(defn process-input [filename]
  (let [[size rovers] (load-input filename)
        plateau (new-plateau size)]
    (process-moves plateau rovers)))

