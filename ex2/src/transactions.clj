(ns transactions)

(defn random-amount []
  (let [r (* 10 (rand-int 50))]
    (if (> r 100) r (random-amount))))

(defn random-amounts []
  (lazy-seq 
   (cons (random-amount) (random-amounts))))

(defn random-txn-type []
  (let [r (rand-int 10)]
    (if (odd? r) :withdrawal :deposit)))

(defn random-txn-types []
  (lazy-seq 
   (cons (random-txn-type) (random-txn-types))))

(defn random-account-id [accounts]
  (:id (accounts (nth (keys accounts) (rand-int (count accounts))))))

(defn random-account-ids [accounts]
  (lazy-seq
   (cons (random-account-id accounts) (random-account-ids accounts))))

(defn bank-transaction [account-id txn-type amount]
  {:id account-id :type txn-type :amount amount})

(defn random-transactions [accounts]
  (map bank-transaction (random-account-ids accounts) (random-txn-types) (random-amounts)))