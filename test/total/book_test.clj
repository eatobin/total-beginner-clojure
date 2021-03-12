;(ns total.book-test
;  (:require [clojure.test :refer [deftest is]]
;            [total.domain :as dom]
;            [total.book :as bk]
;            [clojure.spec.alpha :as s]))
;
;(def br2 {:name "Borrower2" :max-books 2})
;(def bk1 (bk/make-book "Title1" "Author1"))
;(def bk2 (bk/set-borrower bk1 br2))
;
;private val jsonStringBk1: String = "{\"title\":\"Title1\",\"author\":\"Author1\",\"borrower\":null}"
;private val jsonStringBk2: String = "{\"title\":\"Title1\",\"author\":\"Author1\",\"borrower\":{\"name\":\"Borrower2\",\"maxBooks\":2}}"
;private val br2: Borrower = Borrower("Borrower2", 2)
;private val bk1: Book = bookJsonStringToBook(jsonStringBk1).getOrElse(Book("", ""))
;private val bk2: Book = setBorrower(Some(br2), bk1)
;private val bk3: Book = Book("Title3", "Author3")
;
;
;
;(s/conform :unq/book
;           (bk/make-book "Title1" "Author1"))
;(s/conform :unq/book
;           (bk/make-book "Title1" "Author1" br2))
;
;(s/conform (s/cat :title ::dom/title
;                  :author ::dom/author
;                  :borrower (s/? ::dom/maybe-borrower))
;           ["t" "a"])
;
;(s/conform (s/cat :title ::dom/title
;                  :author ::dom/author
;                  :borrower (s/? ::dom/maybe-borrower))
;           ["t" "a" nil])
;
;(s/conform (s/cat :title ::dom/title
;                  :author ::dom/author
;                  :borrower (s/? ::dom/maybe-borrower))
;           ["t" "a" br2])
;
;(deftest get-title-test
;  (is (= "Title1"
;         (bk/get-title bk1))))
;(s/conform ::dom/title
;           (bk/get-title bk1))
;
;(deftest get-author-test
;  (is (= "Author1"
;         (bk/get-author bk1))))
;(s/conform ::dom/author
;           (bk/get-author bk1))
;
;(deftest get-borrower-nil-test
;  (is (nil? (bk/get-borrower bk1))))
;(s/conform ::dom/maybe-borrower
;           (bk/get-borrower bk1))
;
;(deftest get-borrower-someone-test
;  (is (= br2
;         (bk/get-borrower bk2))))
;(s/conform ::dom/maybe-borrower
;           (bk/get-borrower bk2))
;
;(deftest book-to-string-nil-test
;  (is (= "Title1 by Author1; Available"
;         (bk/book-to-string bk1))))
;(s/conform string?
;           (bk/book-to-string bk1))
;
;(deftest book-to-string-someone-test
;  (is (= "Title1 by Author1; Checked out to Borrower2"
;         (bk/book-to-string bk2))))
;(s/conform string?
;           (bk/book-to-string bk2))
