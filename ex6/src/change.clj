(ns change
  (:use utils))

(defn number-of-combinations-for- [amount coin-denoms]
  (cond
   (or (neg? amount) (zero? (count coin-denoms))) 0
   (zero? amount) 1
   :default (+ (number-of-combinations-for- amount (rest coin-denoms))
               (number-of-combinations-for- (- amount (first coin-denoms)) coin-denoms))))

(def number-of-combinations-for- (memoize number-of-combinations-for-))

(defn number-of-combinations-for [dollars & coin-denoms]
  (number-of-combinations-for- (* 100 dollars) coin-denoms))