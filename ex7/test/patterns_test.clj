(ns patterns-test
  (:use patterns clojure.test))

(deftest test-simple-matches
  (is (matches? '?
                '(EQU (COLOR TABLE) RED)))

  (is (matches? 'RED
                'RED))

  (is (not (matches? 'RED
                     '(EQU COLOR BROWN))))

  (is (matches? '(EQU (COLOR TABLE) ?)
                '(EQU (COLOR TABLE) RED)))

  (is (matches? '(EQU (COLOR TABLE) ?)
                '(EQU (COLOR TABLE) (COLOR CHAIR))))

  (is (matches? '(EQU (COLOR ?) ?)
                '(EQU (COLOR TABLE) RED)))

  (is (not (matches? '(EQU (COLOR TABLE) ?)
                     '(EQU (COLOR CHAIR) BROWN))))

  (is (not (matches? '(EQU (COLOR TABLE) ?)
                     '(EQU (COLOR TABLE) RED BROWN)))))