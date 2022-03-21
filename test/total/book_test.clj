(ns total.book-test
  (:require [clojure.test :refer [deftest is]]
            [total.domain :as dom]
            [total.book :as bk]
            [total.borrower :as br]
            [malli.core :as m]
            [malli.generator :as mg]
            [clojure.spec.alpha :as s]))


(def json-string-bk1 "{\"title\":\"Title1\",\"author\":\"Author1\",\"borrower\":null}")
(def json-string-bk2 "{\"title\":\"Title1\",\"author\":\"Author1\",\"borrower\":{\"name\":\"Borrower2\",\"maxBooks\":2}}")
;(def json-string-bees "{\"title\":\"Title1\",\"bees\":\"Knees\",\"author\":\"Author1\",\"borrower\":null}") //fails -extra field
;(def json-string-short "{\"title\":\"Title1\",\"borrower\":null}") //fails spec - no Author
(def br2 {:name "Borrower2" :max-books 2})
(m/validate br/=>borrower br2)
(m/validate bk/=>maybe-borrower br2)

(def bk1 (bk/book-json-string-to-book json-string-bk1))
(m/validate bk/=>book bk1)

(def bk2 (bk/set-borrower bk1 br2))
(m/validate bk/=>book bk2)

;(def bk-bees (bk/book-json-string-to-book json-string-bees))  //fails -extra field

(def =>get-borrower
  (m/schema
    [:=> [:cat bk/=>book] bk/=>maybe-borrower]
    {::m/function-checker mg/function-checker}))
(m/validate =>get-borrower
            bk/get-borrower)

(s/conform (s/cat :title ::dom/title
                  :author ::dom/author
                  :borrower (s/? ::dom/maybe-borrower))
           ["t" "a"])

(s/conform (s/cat :title ::dom/title
                  :author ::dom/author
                  :borrower (s/? ::dom/maybe-borrower))
           ["t" "a" nil])

(s/conform (s/cat :title ::dom/title
                  :author ::dom/author
                  :borrower (s/? ::dom/maybe-borrower))
           ["t" "a" br2])

(deftest get-title-test
  (is (= "Title1"
         (bk/get-title bk1))))
(s/conform ::dom/title
           (bk/get-title bk1))

(deftest get-author-test
  (is (= "Author1"
         (bk/get-author bk1))))
(s/conform ::dom/author
           (bk/get-author bk1))

(deftest get-borrower-nil-test
  (is (nil? (bk/get-borrower bk1))))
(s/conform ::dom/maybe-borrower
           (bk/get-borrower bk1))

(deftest get-borrower-someone-test
  (is (= br2
         (bk/get-borrower bk2))))
(s/conform ::dom/maybe-borrower
           (bk/get-borrower bk2))

(deftest to-string-nil-test
  (is (= "Title1 by Author1; Available"
         (bk/to-string bk1))))
(s/conform string?
           (bk/to-string bk1))

(deftest to-string-someone-test
  (is (= "Title1 by Author1; Checked out to Borrower2"
         (bk/to-string bk2))))
(s/conform string?
           (bk/to-string bk2))

;(deftest book-to-json-string
;  (is (= json-string-bk1
;         (bk/book-to-json-string bk1)))
;  (is (= json-string-bk2
;         (bk/book-to-json-string bk2)))
;  (is (= json-string-bees
;         (bk/book-to-json-string bk-bees))))
;(s/conform string?
;           (bk/book-to-json-string bk1))
;(s/conform string?
;           (bk/book-to-json-string bk2))
;(s/conform string?
;           (bk/book-to-json-string bk-bees))
