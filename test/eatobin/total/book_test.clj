(ns eatobin.total.book-test
  (:require [clojure.test :refer [deftest is]]
            [eatobin.total.borrower :as br]
            [eatobin.total.book :as bk]
            [clojure.spec.alpha :as s]))

(def br2 {::br/name "Borrower2" ::br/maxX-books 2})
(def bk1 (bk/make-book "Title1" "Author1"))
(def bk2 (bk/set-borrower bk1 br2))

(s/conform ::bk/book
           (bk/make-book "Title1" "Author1"))
(s/conform ::bk/book
           (bk/make-book "Title1" "Author1" br2))

(s/conform (s/cat :title ::bk/title
                  :author ::bk/author
                  :borrower (s/? ::bk/maybe-borrower))
           ["t" "a"])

(s/conform (s/cat :title ::bk/title
                  :author ::bk/author
                  :borrower (s/? ::bk/maybe-borrower))
           ["t" "a" nil])

(s/conform (s/cat :title ::bk/title
                  :author ::bk/author
                  :borrower (s/? ::bk/maybe-borrower))
           ["t" "a" br2])

(deftest get-title-test
  (is (= "Title1"
         (bk/get-title bk1))))
(s/conform ::bk/title
           (bk/get-title bk1))

(deftest get-author-test
  (is (= "Author1"
         (bk/get-author bk1))))
(s/conform ::bk/author
           (bk/get-author bk1))

(deftest get-borrower-nil-test
  (is (nil? (bk/get-borrower bk1))))
(s/conform ::bk/maybe-borrower
           (bk/get-borrower bk1))

(deftest get-borrower-someone-test
  (is (= br2
         (bk/get-borrower bk2))))
(s/conform ::bk/maybe-borrower
           (bk/get-borrower bk2))

(deftest book-to-string-nil-test
  (is (= "Title1 by Author1; Available"
         (bk/book-to-string bk1))))
(s/conform string?
           (bk/book-to-string bk1))

(deftest book-to-string-someone-test
  (is (= "Title1 by Author1; Checked out to Borrower2"
         (bk/book-to-string bk2))))
(s/conform string?
           (bk/book-to-string bk2))
