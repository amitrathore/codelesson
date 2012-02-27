(ns map-while)

;; (defn map-while [f pred coll]
;;   (map f (take-while pred coll)))

(defn map-while- [f pred coll results]
  (lazy-seq
   (let [element (first coll)]
     (if (pred element)
       (map-while- f pred (rest coll) (conj results (f element)))
       results))))

(defn map-while [f pred coll]
  (map-while- f pred coll []))