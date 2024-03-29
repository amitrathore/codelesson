(ns test-data
  (:use bank transactions))

(def STARTING-BALANCE 500)

(def B (-> (new-bank 1000 {})
           (add-new-account 1 :checking     [] STARTING-BALANCE "01-01-2009")
           (add-new-account 2 :savings      [] STARTING-BALANCE "02-01-2008")
           (add-new-account 3 :money-market [] STARTING-BALANCE "03-01-2009")
           (add-new-account 4 :checking     [] STARTING-BALANCE "04-01-2009")
           (add-new-account 5 :savings      [] STARTING-BALANCE "05-01-2009")
           (add-new-account 6 :money-market [] STARTING-BALANCE "06-01-2009")))

(def RAND-TX (take 10 (random-transactions (:accounts B))))

(def SPEC-TX [
              {:transaction-id 1 :account-id 1 :transaction-type :withdrawal :amount 200}
              {:transaction-id 2 :account-id 2 :transaction-type :withdrawal :amount 300}
              {:transaction-id 3 :account-id 3 :transaction-type :withdrawal :amount 400}

              {:transaction-id 4 :account-id 4 :transaction-type :deposit :amount 150}
              {:transaction-id 5 :account-id 5 :transaction-type :deposit :amount 250}
              {:transaction-id 6 :account-id 6 :transaction-type :deposit :amount 350}

              {:transaction-id 7 :account-id 3 :transaction-type :withdrawal :amount 400}
              {:transaction-id 8 :account-id 3 :transaction-type :withdrawal :amount 400}
              {:transaction-id 9 :account-id 3 :transaction-type :withdrawal :amount 200}
              {:transaction-id 10 :account-id 3 :transaction-type :withdrawal :amount 200}
              {:transaction-id 11 :account-id 3 :transaction-type :deposit :amount 100}

              {:transaction-id 12 :account-id 2 :transaction-type :deposit :amount 300}
              {:transaction-id 13 :account-id 2 :transaction-type :withdrawal :amount 300}
              {:transaction-id 14 :account-id 2 :transaction-type :deposit :amount 300}
              {:transaction-id 15 :account-id 2 :transaction-type :withdrawal :amount 300}
              {:transaction-id 16 :account-id 2 :transaction-type :deposit :amount 300}
              {:transaction-id 17 :account-id 2 :transaction-type :withdrawal :amount 300}
              {:transaction-id 18 :account-id 2 :transaction-type :deposit :amount 300}
              {:transaction-id 19 :account-id 2 :transaction-type :withdrawal :amount 300}
              {:transaction-id 20 :account-id 2 :transaction-type :deposit :amount 300}
              {:transaction-id 19 :account-id 2 :transaction-type :withdrawal :amount 300}
              {:transaction-id 20 :account-id 2 :transaction-type :deposit :amount 300}
              {:transaction-id 19 :account-id 2 :transaction-type :withdrawal :amount 300}
              {:transaction-id 20 :account-id 2 :transaction-type :deposit :amount 300}


              ])