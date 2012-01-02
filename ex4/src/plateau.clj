(ns plateau
  (:use utils))

(defn new-plateau 
  ([max-x max-y]
     (new-plateau max-x max-y {} {}))
  ([max-x max-y collisions matrix]
     {:max-x max-x :max-y max-y :collisions collisions :matrix matrix}))

(defn position-rover [{:keys [max-x max-y matrix collisions]} {:keys [x y] :as rover}]
  (new-plateau max-x max-y collisions (assoc-in matrix [x y] rover)))

(defn remove-rover [{:keys [max-x max-y collisions matrix]} {:keys [x y]}]
  (->> (dissoc (matrix x) y)
       (assoc matrix x)
       (new-plateau max-x max-y collisions)))

(defn move-rover [old-rover new-rover plateau]
  (-> plateau
      (remove-rover old-rover)
      (position-rover new-rover)))

(defn mark-collision [rover {:keys [collisions] :as p}]
  (merge p {:collisions (assoc collisions (:id rover) rover)}))

(defn with-collision [rover-a rover-b {:keys [collisions] :as plateau}]
  (println "COLLISION! Rovers:" rover-a "AND" rover-b)
  (->> plateau
       (mark-collision rover-a)
       (mark-collision rover-b)))

(defn collides-with-another? [{id :id x :x y :y} {matrix :matrix}]
  (let [occupier (get-in matrix [x y])
        collision (and occupier (not= (:id occupier) id))]
    (if collision occupier)))

(defn has-collided? [{id :id} {collisions :collisions}]
  (collisions id))

(defn collision-checked [rover plateau]
  (let [collided (collides-with-another? rover plateau)]
    (if-not collided
      plateau
      (with-collision rover collided plateau))))

(defn is-within-bounds? [{:keys [x y]} {:keys [max-x max-y]}]
  (and (>= x 1) (>= y 1) (>= max-x x) (>= max-y y)))

(def out-of-bounds? (complement is-within-bounds?))

(defn all-rovers [{matrix :matrix}]
  (->> matrix vals (mapcat vals) (sort-by :id)))

(defn report-rovers [{:keys [collisions] :as plateau}]
  (println "Final POSITIONS:")
  (doseq [{:keys [id x y bearing]} (all-rovers plateau)]
    (println "ID:" id "x, y, bearing:" x y bearing))
  (println "Final COLLISIONS:")
  (doseq [collided (vals collisions)]
    (println "Collided:" collided)))
