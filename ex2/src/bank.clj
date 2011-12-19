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

(defmulti overdraft-percentage-fee :type)

(defmethod overdraft-percentage-fee :checking [account] 0.10)

(defmethod overdraft-percentage-fee :savings [account] 0.075)

(defmethod overdraft-percentage-fee :money-market [account] 0.05)

(defn needs-overdraw-processing? [transaction-type old-balance new-balance]
  (let [needs? (and (= :withdrawal transaction-type) (neg? new-balance) (< new-balance old-balance))]
    (println "Needs overdraw processing:" needs?)
    needs?))

(defn- compute-overdraft-withdraw [old-balance new-balance transaction-type 
                                   amount  percentage-fee overall-overdraft]
  (if-not (needs-overdraw-processing? transaction-type old-balance new-balance)
    [overall-overdraft 0]
    (let [amount-needed (if (neg? old-balance) amount (- amount old-balance))
          new-overall-overdraft (- overall-overdraft amount-needed)]
      (println "New-balance:" new-balance "Exceeds max:" (<= new-balance MAX-OVERDRAWN-BALANCE))
      (if (or (neg? new-overall-overdraft) (<= new-balance MAX-OVERDRAWN-BALANCE))
        [overall-overdraft BASE-OVERDRAFT-FEE]
        [new-overall-overdraft (* amount-needed percentage-fee)]))))

(defn- compute-overdraft-deposit [transaction-type old-balance amount overall-overdraft]
  (if-not (and (= :deposit transaction-type) (neg? old-balance))
    overall-overdraft
    (+ overall-overdraft amount)))

(defn run-txn [{:keys [overall-overdraft] :as bank} 
               {:keys [id type transactions balance] :as account} 
               {:keys [transaction-type amount] :as transaction} 
               compute]
  (println "----------\nBank overall:" overall-overdraft)
  (println "Account ID:" id "Balance:" balance "Transaction:" transaction-type amount)
  (let [new-account-balance (compute balance amount)
        [new-overall-overdraft overdraft-fee] (compute-overdraft-withdraw balance new-account-balance 
                                                                          transaction-type amount 
                                                                          (overdraft-percentage-fee account) 
                                                                          overall-overdraft)
        new-overall-overdraft (compute-overdraft-deposit transaction-type balance amount new-overall-overdraft)]
    [new-overall-overdraft (-> account
                               (assoc :balance (- new-account-balance overdraft-fee))
                               (assoc :transactions (conj transactions transaction)))]))

(defmulti update-account (fn [bank accnt txn] (:transaction-type txn)))

(defmethod update-account :withdrawal [bank account transaction]
  (run-txn bank account transaction -))

(defmethod update-account :deposit [bank account transaction]  
  (run-txn bank account transaction +))

(defn print-account [{:keys [id type transactions balance] :as account}]
  (println "Account ID:" id type)
  (println "Balance:" balance)
  (println "Transactions:")
  (println (str-join "\n" transactions))
  (println "----------------"))