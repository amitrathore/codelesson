(ns plateau)

(defn new-plateau 
  ([max-x max-y]
     (new-plateau max-x max-y {}))
  ([max-x max-y matrix]
     {:max-x max-x :max-y max-y :matrix matrix :collisions {}}))

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

(defn mark-collision [rover {:keys [collisions] :as p}]
  (merge p {:collisions (assoc collisions (:id rover) rover)}))

(defn with-collision [rover-a rover-b {:keys [collisions] :as p}]
  (println "COLLISION! Rovers:" rover-a "AND" rover-b)
  (merge p (->> p
               (mark-collision rover-a)
               (mark-collision rover-b))))

(defn collision? [{id :id x :x y :y} {matrix :matrix}]
  (let [occupier (get-in matrix [x y])
        collision (and occupier (not= (:id occupier) id))]
    (if collision occupier)))

(defn collision-checked [rover plateau]
  (let [collided (collision? rover plateau)]
    (if-not collided
      plateau
      (with-collision rover collided plateau))))

(defn report-rovers [{matrix :matrix}]
  (doseq [row (keys matrix)]
    (doseq [{:keys [id x y bearing] :as v} (vals (matrix row))]
      (if v (println "ID:" id "x, y, bearing:" x y bearing)))))
