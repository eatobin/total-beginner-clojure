(ns total.borrower-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is]]
            [total.borrower :as br]
            [total.domain :as dom]))

(def json-string-br "{\"name\":\"Borrower1\",\"maxBooks\":1}")
(def br1 (br/borrower-json-string-to-borrower json-string-br))
(s/conform :unq/borrower
           br1)

(s/valid? ::dom/extract-fn-br-name
          br/get-name)

(s/valid? ::dom/extract-fn-bk-title
          br/get-name)

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

(deftest set-name-test
  (is (= {:name "Jack", :max-books 1}
         (br/set-name br1 "Jack"))))
(s/conform :unq/borrower
           (br/set-name br1 "Jack"))

(deftest set-max-books-test
  (is (= {:name "Borrower1", :max-books 10}
         (br/set-max-books br1 10))))
(s/conform :unq/borrower
           (br/set-max-books br1 10))

(deftest to-string-test
  (is (= "Borrower1 (1 books)"
         (br/to-string br1))))
(s/conform string?
           (br/to-string br1))

(deftest borrower-to-json-string
  (is (= json-string-br
         (br/borrower-to-json-string br1))))
(s/conform string?
           (br/borrower-to-json-string br1))
