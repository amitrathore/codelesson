(ns plateau)

(defn new-plateau 
  ([max-x max-y]
     {:max-x max-x :max-y max-y :matrix {}})
  ([max-x max-y matrix]
     {:max-x max-x :max-y max-y :matrix matrix}))

(defn place-rover [[x y bearing :as rover] {:keys [max-x max-y matrix]}]
  (new-plateau max-x max-y (assoc-in matrix [x y] rover)))

