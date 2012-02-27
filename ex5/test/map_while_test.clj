(ns map-while-test
  (:use map-while clojure.test))

(deftest test-map-while
  (is (= 100 (count (map-while identity #(< % 100) (range 200)))))
  (is (= 100000 (count (map-while identity #(< % 100000) (range 200000))))))