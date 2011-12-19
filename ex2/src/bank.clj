(ns bank
  (:use clojure.contrib.str-utils))

(declare update-account print-account)

(def BASE-OVERDRAFT-FEE 10)

(def MAX-OVERDRAWN-BALANCE -1000)

;;;;;; bank related

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
        [updated-overdraft updated-account] (update-account bank account-to-update transaction)]
    (new-bank updated-overdraft (assoc accounts account-id updated-account))))

(defn process-transactions [bank transactions]
  (reduce process-transaction bank transactions))

(defn print-bank [{:keys [overall-overdraft accounts]}]
  (println "************\nOVER-DRAFT:" overall-overdraft)
  (println "ACCOUNTS:\n----------")
  (doseq [a (vals accounts)] 
    (print-account a)))


;;;;;; accounts related

(def overdraft-percentage-fee 
     {:checking     0.100
      :savings      0.075
      :money-market 0.050})

(defn withdrawal-scenario [{:keys [overall-overdraft] :as bank} 
               {:keys [balance] :as account} 
               {:keys [transaction-type amount] :as transaction}
               new-balance
               overdraft-amount]
  (cond
   (pos? new-balance) :no-overdraft
   (and (neg? new-balance) 
        (< new-balance MAX-OVERDRAWN-BALANCE) 
        (> overall-overdraft overdraft-amount)) :overdraft-available
   (and (neg? new-balance) 
        (> new-balance MAX-OVERDRAWN-BALANCE)
        (< overall-overdraft overdraft-amount)) :overdraft-unavailable
   (and (neg? new-balance)
        (< new-balance MAX-OVERDRAWN-BALANCE)) :overdraft-unavailable))

(defn deposit-scenario [{:keys [overall-overdraft] :as bank} 
                        {:keys [balance] :as account} 
                        {:keys [transaction-type amount] :as transaction}
                        new-balance
                        repay-amount]
  (cond
   (or (pos? balance) (zero? balance)) :no-repayment
   (neg? balance) :repayment))

(defmulti run-withdrawal withdrawal-scenario)

(defmethod run-withdrawal :no-overdraft [{:keys [overall-overdraft] :as bank} 
                                         {:keys [type balance] :as account} 
                                         {:keys [transaction-type amount] :as transaction}
                                         new-balance
                                         overdraft-amount]
  [new-balance overall-overdraft 0])

(defmethod run-withdrawal :overdraft-available [{:keys [overall-overdraft] :as bank} 
                                                {:keys [balance] :as account} 
                                                {:keys [transaction-type amount] :as transaction}
                                                new-balance
                                                overdraft-amount]
  [new-balance (- overall-overdraft overdraft-amount) (* overdraft-amount (overdraft-percentage-fee type))])

(defmethod run-withdrawal :overdraft-unavailable [{:keys [overall-overdraft] :as bank} 
                                                  {:keys [balance] :as account} 
                                                  transaction
                                                  new-balance
                                                  overdraft-amount]
  [balance overall-overdraft BASE-OVERDRAFT-FEE])



(defmulti run-deposit deposit-scenario)

(defmethod run-deposit :no-repayment [{:keys [overall-overdraft] :as bank} 
                                      {:keys [balance] :as account} 
                                      {:keys [transaction-type amount] :as transaction}
                                      new-balance
                                      repay-amount]
  [new-balance overall-overdraft 0])

(defmethod run-deposit :repayment [{:keys [overall-overdraft] :as bank} 
                                   {:keys [balance] :as account} 
                                   {:keys [transaction-type amount] :as transaction}
                                   new-balance
                                   repay-amount]
  (let [repayment (if (neg? balance) (+ amount balance))])
  [new-balance overall-overdraft 0])

;; overdraft-amount (if (neg? balance) amount (- amount balance))


;; (defmulti update-account (fn [bank accnt txn] (:transaction-type txn)))

;; (defmethod update-account :withdrawal [bank account transaction]
;;   (run-txn bank account transaction -))

;; (defmethod update-account :deposit [bank account transaction]  
;;   (run-txn bank account transaction +))

(defn print-account [{:keys [id type transactions balance] :as account}]
  (println "Account ID:" id type)
  (println "Balance:" balance)
  (println "Transactions:")
  (println (str-join "\n" transactions))
  (println "----------------"))