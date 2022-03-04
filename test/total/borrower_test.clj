(ns total.borrower-test
  (:require [clojure.test :refer [deftest is]]
            [total.borrower :as br]
            [malli.core :as m]
            [malli.generator :as mg]))

(def json-string-br "{\"name\":\"Borrower1\",\"maxBooks\":1}")
(def br1 (br/borrower-json-string-to-borrower json-string-br))
(m/validate br/=>borrower br1)

(def =>get-name
  (m/schema
    [:=> [:cat br/=>borrower] br/=>name]
    {::m/function-checker mg/function-checker}))
(m/validate =>get-name
            br/get-name)

(deftest get-name-test
  (is (= "Borrower1"
         (br/get-name br1))))
(m/validate br/=>name
            (br/get-name br1))

(deftest get-max-books-test
  (is (= 1
         (br/get-max-books br1))))
(m/validate br/=>max-books
            (br/get-max-books br1))

(deftest set-name-test
  (is (= {:name "Jack", :max-books 1}
         (br/set-name br1 "Jack"))))
(m/validate br/=>borrower
            (br/set-name br1 "Jack"))

(deftest set-max-books-test
  (is (= {:name "Borrower1", :max-books 11}
         (br/set-max-books br1 11))))
(m/validate br/=>borrower
            (br/set-max-books br1 11))

(deftest to-string-test
  (is (= "Borrower1 (1 books)"
         (br/to-string br1))))
(m/validate :string
            (br/to-string br1))

(deftest borrower-to-json-string
  (is (= json-string-br
         (br/borrower-to-json-string br1))))
(m/validate :string
            (br/borrower-to-json-string br1))
