(ns bank
  (:use account))


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
  (println "Overdraft:" overall-overdraft)
  (println "Accounts:\n-----------")
  (doseq [a (vals accounts)] 
    (print-account a)))