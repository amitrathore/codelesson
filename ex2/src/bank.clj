(ns bank
  (:use utils clojure.contrib.str-utils)
  (:import [java.util Date]))

(declare update-account print-account transaction-results)

;;;;;; bank related

(def BASE-OVERDRAFT-FEE 10)

(def MAX-OVERDRAWN-BALANCE -1000)

(def INTEREST-EVERY 6)

(def BONUS-EVERY 14)

(def overdraft-percentage-fee 
     {:checking     0.100
      :savings      0.075
      :money-market 0.050})

(defn interest-percentage [{type :type}]
  ({:checking 0.02 :savings 0.04 :money-market 0.06} type))

(defn years-since [start-date]
  (-> start-date (days-between (Date. (System/currentTimeMillis))) (/ 356) int))

(defn age-profile [{start-date :start-date}]
  (let [age (years-since start-date)]
    (cond 
     (<= age 2)                 :young-age
     (and (> age 2) (< age 5))  :middle-age
     (>= age 5)                 :old-age)))

(defn balance-profile [{:keys [balance] :as account}]
  (if (< balance 500) ::low-balance ::high-balance))

(defn bonus-profile [account]
  [(age-profile account) (balance-profile account)])

(defmulti bonus-percentage bonus-profile)

(defmethod bonus-percentage [:middle-age ::low-balance] [account]
  (/ (interest-percentage account) 32))

(defmethod bonus-percentage [:middle-age ::high-balance] [account]
  (/ (interest-percentage account) 24))

(defmethod bonus-percentage [:old-age ::low-balance] [account]
  (/ (interest-percentage account) 16))

(defmethod bonus-percentage [:old-age ::high-balance] [account]
  (/ (interest-percentage account) 8))

(defmethod bonus-percentage :default [_]
  0)

(defn new-bank 
  ([overdraft-balance accounts]
     {:overall-overdraft overdraft-balance :accounts accounts})
  ([]
     (new-bank 100000 {})))

(defn new-account [account-id account-type transactions balance start-date]
  {:id account-id :type account-type :transactions transactions :balance balance :start-date start-date})

(defn add-new-account [bank account-id account-type transactions balance start-date-string]
  (->> (new-account account-id account-type transactions balance (date-from-str "MM-dd-yyyy" start-date-string))
       (assoc (:accounts bank) account-id)
       (new-bank (:overall-overdraft bank))))

(defn process-transaction [{:keys [accounts] :as bank} {:keys [account-id] :as transaction}]
  (let [account-to-update (accounts account-id)
        [updated-overdraft updated-account] (transaction-results bank account-to-update transaction)]
    (new-bank updated-overdraft (assoc accounts account-id updated-account))))

(defn process-transactions [bank transactions]
  (reduce process-transaction bank transactions))

(defn print-bank [{:keys [overall-overdraft accounts]}]
  (println "************\nOVER-DRAFT:" overall-overdraft)
  (println "ACCOUNTS:\n----------")
  (doseq [a (vals accounts)] 
    (print-account a)))


;;;;;; accounts related

(defn account-overdraft-limit-reached? [new-balance]
  (< new-balance MAX-OVERDRAWN-BALANCE))

(defn withdrawal-scenario [{overall-overdraft :overall-overdraft} account transaction new-balance overdraft-amount]
  (cond
   (pos? new-balance)                                                     :no-overdraft
   (and (neg? new-balance) 
        (not (account-overdraft-limit-reached? new-balance))
        (> overall-overdraft overdraft-amount))                           :overdraft-available
   (and (neg? new-balance) 
        (account-overdraft-limit-reached? new-balance)
        (< overall-overdraft overdraft-amount))                           :overdraft-unavailable
   (and (neg? new-balance)
        (< new-balance MAX-OVERDRAWN-BALANCE))                            :overdraft-unavailable))

(defn deposit-scenario [bank {balance :balance} transaction new-balance]
  (cond
   (or (pos? balance) (zero? balance))     :no-repayment
   (neg? balance)                          :repayment))

(defmulti run-withdrawal withdrawal-scenario)

(defmethod run-withdrawal :no-overdraft [{:keys [overall-overdraft] :as bank} account transaction new-balance overdraft-amount]
  [new-balance overall-overdraft 0])

(defmethod run-withdrawal :overdraft-available [bank account transaction new-balance overdraft-amount]
  [new-balance (- (:overall-overdraft bank) overdraft-amount) (* overdraft-amount (overdraft-percentage-fee (:type account)))])

(defmethod run-withdrawal :overdraft-unavailable [bank account transaction new-balance overdraft-amount]
  [(:balance account) (:overall-overdraft bank) BASE-OVERDRAFT-FEE])

(defn repayment-amount [balance transaction-amount]
  (let [current-overdraw (Math/abs balance)]
    (if (and (neg? balance) (< current-overdraw transaction-amount)) current-overdraw transaction-amount)))

(defmulti run-deposit deposit-scenario)

(defmethod run-deposit :no-repayment [bank account transaction new-balance]
  [new-balance (:overall-overdraft bank)])

(defmethod run-deposit :repayment [bank account transaction new-balance]
  [new-balance (+ (:overall-overdraft bank) (repayment-amount (:balance account) (:amount transaction)))])

(defn n-transactions? [{transactions :transactions} n]
  (zero? (mod (count transactions) n)))

(defn ready-for-interest [account]
  (n-transactions? account INTEREST-EVERY))

(defn ready-for-bonus [account]
  (n-transactions? account BONUS-EVERY))

(defn compute-addition [{:keys [balance] :as account} percentage-fn]
  (+ balance (* balance (percentage-fn account))))

(defn compute-interest [account]
  (compute-addition account interest-percentage))

(defn compute-bonus [account]
  (compute-addition account bonus-percentage))

(defn process-addition [addition-predicate compute-addition-fn account]
  (if-not (and (pos? (:balance account)) (addition-predicate account))
    account
    (-> account
        (assoc :balance (compute-addition-fn account)))))

(defn process-interest [account]
  (process-addition ready-for-interest compute-interest account))

(defn process-bonus [account]
  (process-addition ready-for-bonus compute-bonus account))

(defn compute-new-balance [{:keys [balance]} {:keys [transaction-type amount]}]
  (condp = transaction-type
    :withdrawal (- balance amount)
    :deposit (+ balance amount)))

(defn update-account [{:keys [transactions] :as account} new-balance new-transaction]
  (-> account
      (assoc :balance new-balance)
      (assoc :transactions (conj transactions new-transaction))
      process-interest
      process-bonus))

(defn transaction-results [bank {:keys [balance transactions] :as account} {:keys [amount] :as transaction}]
  (let [new-balance (compute-new-balance account transaction)
        overdraft-amount (if (neg? balance) amount (- amount balance))]
    (condp = (:transaction-type transaction)
      :withdrawal (let [[new-balance new-overall overdraft-fee] (run-withdrawal bank account transaction new-balance overdraft-amount)]
                    [new-overall (update-account account (- new-balance overdraft-fee) transaction)])
      :deposit    (let [[new-balance new-overall] (run-deposit bank account transaction new-balance)]
                    [new-overall (update-account account new-balance transaction)]))))

(defn print-account [{:keys [id type transactions balance start-date] :as account}]
  (println "Account ID:" id type "Age:" (years-since start-date))
  (println "Balance:" balance)
  (println "Transactions" (count transactions) "->")
  (println (str-join "\n" transactions))
  (println "----------------"))