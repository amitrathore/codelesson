(ns tests
  (:use [clojure.test :only [run-tests deftest is are testing]]
        [plateau])
  (:require [clojure.contrib.duck-streams :as ducky]))

(deftest test-plateau-basics 
  (let [p (new-plateau 5 5)]
    (println (position-rover p [2 3 \N]))))
