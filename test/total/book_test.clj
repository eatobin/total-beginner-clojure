(ns total.book-test
  (:require [clojure.test :refer [deftest is]]
            [total.borrower :as br]
            [total.book :as bk]
            [malli.core :as m]
            [malli.generator :as mg]))


(def json-string-bk1 "{\"title\":\"Title1\",\"author\":\"Author1\",\"borrower\":null}")
(def json-string-bk2 "{\"title\":\"Title1\",\"author\":\"Author1\",\"borrower\":{\"name\":\"Borrower2\",\"maxBooks\":2}}")
(def br2 {:name "Borrower2" :max-books 2})
(m/validate br/=>borrower br2)
(m/validate bk/=>maybe-borrower br2)

(def bk1 (bk/book-json-string-to-book json-string-bk1))
(m/validate bk/=>book bk1)

(def bk2 (bk/set-borrower bk1 br2))
(m/validate bk/=>book bk2)

(def =>get-borrower
  (m/schema
    [:=> [:cat bk/=>book] bk/=>maybe-borrower]
    {::m/function-checker mg/function-checker}))
(m/validate =>get-borrower
            bk/get-borrower)

(deftest get-title-test
  (is (= "Title1"
         (bk/get-title bk1))))

(deftest get-author-test
  (is (= "Author1"
         (bk/get-author bk1))))

(deftest get-borrower-nil-test
  (is (nil? (bk/get-borrower bk1))))

(deftest get-borrower-someone-test
  (is (= br2
         (bk/get-borrower bk2))))

(deftest to-string-nil-test
  (is (= "Title1 by Author1; Available"
         (bk/to-string bk1))))

(deftest to-string-someone-test
  (is (= "Title1 by Author1; Checked out to Borrower2"
         (bk/to-string bk2))))

(deftest book-to-json-string
  (is (= json-string-bk1
         (bk/book-to-json-string bk1)))
  (is (= json-string-bk2
         (bk/book-to-json-string bk2))))
