(ns account
  (:use clojure.contrib.str-utils))

(defn print-account [{:keys [id type transactions balance] :as account}]
  (println "Account ID:" id type)
  (println "Balance:" balance)
  (println "Transactions:")
  (println (str-join "\n" transactions))
  (println "----------------"))


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
