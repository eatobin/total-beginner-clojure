;; clojure -M:project/outdated
;; clojure -M:test/kaocha-plain

(ns total.borrower-test
  (:require [clojure.test :refer [deftest is]]
            [malli.core :as m]
            [malli.generator :as mg]
            [total.borrower :as br]))

(def json-string-br "{\"name\":\"Borrower1\",\"maxBooks\":1}")
(def br1 (br/borrower-json-string-to-borrower json-string-br))
(m/explain br/=>name "")
(m/explain br/=>name 0)
(m/explain br/=>max-books 0)
(m/explain br/=>max-books 80)
(m/explain br/=>max-books "0")
(m/validate br/=>borrower br1)
(m/explain br/=>borrower br1)
(m/explain br/=>borrower br1)
(m/explain br/=>borrower {:name "" :max-books 3})
(m/explain br/=>borrower {:name "me" :max-books 0})
(m/explain br/=>borrower {:name "me" :max-books 50})
(m/explain br/=>borrower {:name "" :max-books 0})
(m/explain br/=>borrower {:name "me" :max-books "you"})
(m/explain br/=>borrower {:name "" :max-books ""})

(def =>get-name-test
  (m/schema
   [:=> [:cat br/=>borrower] br/=>name]
   {::m/function-checker mg/function-checker}))
(m/validate =>get-name-test
            br/get-name)
(m/validate =>get-name-test
            br/get-max-books)
(m/explain =>get-name-test
           br/get-max-books)

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
  (is (= {:name "Borrower1", :max-books 10}
         (br/set-max-books br1 10))))
(m/validate br/=>borrower
            (br/set-max-books br1 10))

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
