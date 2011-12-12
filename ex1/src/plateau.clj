(ns plateau)

(defn new-plateau 
  ([max-x max-y]
     (new-plateau max-x max-y {}))
  ([max-x max-y matrix]
     {:max-x max-x :max-y max-y :matrix matrix}))

(defn position-rover [{:keys [max-x max-y matrix]} [x y _ :as rover]]
  (new-plateau max-x max-y (assoc-in matrix [x y] rover)))

(defn remove-rover [{:keys [max-x max-y matrix]} [x y _]]
  (->> (assoc matrix x (dissoc (matrix x) y))
       (new-plateau max-x max-y)))

(defn move-rover [[x y _ :as old-rover] new-rover plateau]
  (-> plateau
      (remove-rover old-rover)
      (position-rover new-rover)))

(defn report-rovers [{:keys [_ _ matrix]}]
  (doseq [row (keys matrix)]
    (doseq [[x y bearing :as v] (vals (matrix row))]
      (if v (println "x, y, bearing:" x y bearing)))))
