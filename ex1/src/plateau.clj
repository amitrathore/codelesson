(ns plateau)

(defn new-plateau 
  ([max-x max-y]
     (new-plateau max-x max-y {}))
  ([max-x max-y matrix]
     {:max-x max-x :max-y max-y :matrix matrix}))

(defn position-rover [{:keys [max-x max-y matrix]} {:keys [x y] :as rover}]
  (new-plateau max-x max-y (assoc-in matrix [x y] rover)))

(defn remove-rover [{:keys [max-x max-y matrix]} {:keys [x y]}]
  (->> (dissoc (matrix x) y)
       (assoc matrix x)
       (new-plateau max-x max-y)))

(defn move-rover [old-rover new-rover plateau]
  (-> plateau
      (remove-rover old-rover)
      (position-rover new-rover)))

(defn report-rovers [{matrix :matrix}]
  (doseq [row (keys matrix)]
    (doseq [{:keys [id x y bearing] :as v} (vals (matrix row))]
      (if v (println "ID:" id "x, y, bearing:" x y bearing)))))
