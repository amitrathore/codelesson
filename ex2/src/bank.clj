(ns bank)

(defn new-bank 
  ([overdraft-balance accounts]
     {:overall-overdraft overdraft-balance :accounts accounts})
  ([]
     (new-bank 100000 {})))

(defn new-account [bank id account-type balance]
  (->> {:id id :type account-type :balance balance}
       (assoc (:accounts bank) id)
       (new-bank (:overall-overdraft bank))))

(defn update-account [bank id new-balance]
  (new-account bank id (:type (bank id)) new-balance))

(defn process-amount-change [change-fn bank {:keys [id amount]}]
  (let [new-balance (-> (:accounts bank) id :balance (change-fn amount))]
    (update-account bank id new-balance)))

(defmulti apply-transaction (fn [_ txn] (:type txn)))

(defmethod apply-transaction :withdrawal [bank transaction]
  (process-amount-change - bank transaction))

(defmethod apply-transaction :deposit [bank transaction]
  (process-amount-change + bank transaction))

(defmethod apply-transaction :default [b t]
  (println "Should never come here [_ t]:" t ))

(defn reducer [bank transaction]
  (println "Processing" transaction)
  (apply-transaction bank transaction))

(defn process-transactions [bank transactions]
  (reduce reducer bank transactions))