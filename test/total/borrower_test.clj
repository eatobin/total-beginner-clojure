(ns total.borrower-test
  (:require [clojure.test :refer [deftest is]]
            [total.domain :as dom]
            [total.borrower :as br]
            [clojure.spec.alpha :as s]))

(def json-string-br "{\"name\":\"Borrower1\",\"maxBooks\":1}")
(def br1 (br/borrower-json-string-to-borrower json-string-br))
(s/conform :unq/borrower
           br1)

(deftest get-name-test
  (is (= "Borrower1"
         (br/get-name br1))))
(s/conform ::dom/name
           (br/get-name br1))

(deftest get-max-books-test
  (is (= 1
         (br/get-max-books br1))))
(s/conform ::dom/max-books
           (br/get-max-books br1))

;(deftest set-name-test
;  (is (= {:name "Jack", :max-books 1}
;         (br/set-name br1 "Jack"))))
;(s/conform :unq/borrower
;           (br/set-name br1 "Jack"))
;
;(deftest set-max-books-test
;  (is (= {:name "Borrower1", :max-books 11}
;         (br/set-max-books br1 11))))
;(s/conform :unq/borrower
;           (br/set-max-books br1 11))
;
;(deftest borrower-to-string-test
;  (is (= "Borrower1 (1 books)"
;         (br/to-string br1))))
;(s/conform string?
;           (br/to-string br1))
