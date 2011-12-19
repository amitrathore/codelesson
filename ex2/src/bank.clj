(ns bank
  (:use utils clojure.contrib.str-utils))

(declare update-account print-account transaction-results)

;;;;;; bank related

(def BASE-OVERDRAFT-FEE 10)

(def MAX-OVERDRAWN-BALANCE -1000)

(def overdraft-percentage-fee 
     {:checking     0.100
      :savings      0.075
      :money-market 0.050})

(defn new-bank 
  ([overdraft-balance accounts]
     {:overall-overdraft overdraft-balance :accounts accounts})
  ([]
     (new-bank 100000 {})))

(defn add-new-account [bank account-id account-type transactions balance]
  (->> {:id account-id :type account-type :transactions transactions :balance balance}
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

(defn withdrawal-scenario [{:keys [overall-overdraft] :as bank} account transaction new-balance overdraft-amount]
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

(defn compute-new-balance [{:keys [balance]} {:keys [transaction-type amount]}]
  (condp = transaction-type
    :withdrawal (- balance amount)
    :deposit (+ balance amount)))

(defn update-account [{:keys [transactions] :as account} new-balance new-transaction]
  (-> account
      (assoc :balance new-balance)
      (assoc :transactions (conj transactions new-transaction))))

(defn transaction-results [bank {:keys [balance transactions] :as account} {:keys [amount] :as transaction}]
  (let [new-balance (compute-new-balance account transaction)
        overdraft-amount (if (neg? balance) amount (- amount balance))]
    (condp = (:transaction-type transaction)
      :withdrawal (let [[new-balance new-overall overdraft-fee] (run-withdrawal bank account transaction new-balance overdraft-amount)]
                    [new-overall (update-account account (- new-balance overdraft-fee) transaction)])
      :deposit    (let [[new-balance new-overall] (run-deposit bank account transaction new-balance)]
                    [new-overall (update-account account new-balance transaction)]))))

(defn print-account [{:keys [id type transactions balance] :as account}]
  (println "Account ID:" id type)
  (println "Balance:" balance)
  (println "Transactions:")
  (println (str-join "\n" transactions))
  (println "----------------"))