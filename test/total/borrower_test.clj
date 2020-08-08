(ns total.borrower-test
  (:require [clojure.test :refer :all]
            [total.borrower :as br]
            [clojure.spec.alpha :as s]))

(def br1 (br/make-borrower "Borrower1" 1))
(s/conform ::br/borrower
           (br/make-borrower "Borrower1" 1))

(deftest get-name-test
  (is (= "Borrower1"
         (br/get-name br1))))
(s/conform ::br/name
           (br/get-name br1))

(deftest get-maxX-books-test
  (is (= 1
         (br/get-maxX-books br1))))
(s/conform ::br/maxX-books
           (br/get-maxX-books br1))

(deftest set-name-test
  (is (= {::br/name "Jack", ::br/maxX-books 1}
         (br/set-name br1 "Jack"))))
(s/conform ::br/borrower
           (br/set-name br1 "Jack"))

(deftest set-maxX-books-test
  (is (= {::br/name "Borrower1", ::br/maxX-books 11}
         (br/set-maxX-books br1 11))))
(s/conform ::br/borrower
           (br/set-maxX-books br1 11))

(deftest borrower-to-string-test
  (is (= "Borrower1 (1 books)"
         (br/borrower-to-string br1))))
(s/conform string?
           (br/borrower-to-string br1))
