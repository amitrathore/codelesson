(ns bank
  (:use clojure.contrib.str-utils))

(declare update-account print-account)

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
  (println "OVER-DRAFT:" overall-overdraft)
  (println "ACCOUNTS:\n----------")
  (doseq [a (vals accounts)] 
    (print-account a)))


;;;;;; accounts related

(defn run-txn [{:keys [overall-overdraft] :as bank} 
               {:keys [transactions balance] :as account} 
               {:keys [amount] :as transaction} 
               compute]
  [overall-overdraft (-> account
                         (assoc :balance (compute balance amount))
                         (assoc :transactions (conj transactions transaction)))])

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