(ns change-test
  (:use change utils clojure.test))

(deftest test-simple-change
  (is (= 1 (number-of-combinations-for  0 50 25)))
  (is (= 1 (number-of-combinations-for 10 50)))
  (is (= 292 (number-of-combinations-for 1 50 25 10 5 1)))
  (is (= 2728 (number-of-combinations-for 2 100 50 25 10 5 1)))
  (is (= 111022 (number-of-combinations-for 5 200 100 50 25 10 5 1))))