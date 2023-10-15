(ns total.library-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer [deftest is]]
   [total.book :as bk]
   [total.borrower :as br]
   [total.library :as lib]
   [total.total :as tot]))

(def br1 {:name "Borrower1" :max-books 1})
(def br2 {:name "Borrower2" :max-books 2})
(def br3 {:name "Borrower3" :max-books 3})

(def brs1 (list br1 br2))
(def brs2 (list br2 br1 br3))

(def bk1 {:title "Title1", :author "Author1", :maybe-borrower br1})
(def bk2 {:title "Title2", :author "Author2", :maybe-borrower nil})
(def bk3 {:title "Title3", :author "Author3", :maybe-borrower br3})
(def bk4 {:title "Title4", :author "Author4", :maybe-borrower br3})

(def bks1 (list bk2 bk1))
(def bks2 (list bk1 bk2 bk3))
(def bks3 (list bk1 bk2 bk3 bk4))
(def bks4 (list bk2 {:title "Title1", :author "Author1", :maybe-borrower nil}))
(def bks5 (list bk1 bk2))

(def json-string-borrowers "[{\"maxBooks\":2, \"name\":\"Borrower2\"},{\"name\":\"Borrower1\",\"maxBooks\":1}]")
(def json-string-books "[{\"title\":\"Title1\",\"author\":\"Author1\",\"borrower\":{\"name\":\"Borrower1\",\"maxBooks\":1}},{\"title\":\"Title2\",\"author\":\"Author2\",\"borrower\":null}]")

(deftest add-borrower-pass-test
  (is (= brs2
         (lib/add-item br3 brs1))))
(s/conform :unq/brs
           (lib/add-item br3 brs1))

(deftest add-book-pass-test
  (is (= bks2
         (lib/add-item bk3 bks1))))
(s/conform :unq/bks
           (lib/add-item bk3 bks1))

(deftest add-borrower-fail-test
  (is (= brs1
         (lib/add-item br2 brs1))))
(s/conform :unq/brs
           (lib/add-item br2 brs1))

(deftest add-book-fail-test
  (is (= bks1
         (lib/add-item bk2 bks1))))
(s/conform :unq/bks
           (lib/add-item bk2 bks1))

(deftest remove-book-pass-test
  (is (= bks1
         (lib/remove-book bk3 bks2))))
(s/conform :unq/bks
           (lib/remove-book bk3 bks2))

(deftest remove-book-fail-test
  (is (= bks5
         (lib/remove-book bk3 bks1))))
(s/conform :unq/bks
           (lib/remove-book bk3 bks1))

(deftest find-book-pass-test
  (is (= bk1
         (lib/find-item "Title1" bks2 bk/get-title))))
(s/conform (s/or :found-br :unq/borrower
                 :found-bk :unq/book
                 :not-found nil?)
           (lib/find-item "Title1" bks2 bk/get-title))

(deftest find-book-fail-test
  (is (= nil
         (lib/find-item "Title11" bks2 bk/get-title))))
(s/conform (s/or :found-br :unq/borrower
                 :found-bk :unq/book
                 :not-found nil?)
           (lib/find-item "Title11" bks2 bk/get-title))

(deftest find-borrower-pass-test
  (is (= br1
         (lib/find-item "Borrower1" brs2 br/get-name))))
(s/conform (s/or :found-br :unq/borrower
                 :found-bk :unq/book
                 :not-found nil?)
           (lib/find-item "Borrower1" brs2 br/get-name))

(deftest find-borrower-fail-test
  (is (= nil
         (lib/find-item "Borrower11" brs1 br/get-name))))
(s/conform (s/or :found-br :unq/borrower
                 :found-bk :unq/book
                 :not-found nil?)
           (lib/find-item "Borrower11" brs1 br/get-name))

(deftest get-books-for-borrower-0-books-test
  (is (= []
         (lib/get-books-for-borrower br2 bks1))))
(s/conform :unq/bks
           (lib/get-books-for-borrower br2 bks1))

(deftest get-books-for-borrower-1-book-test
  (is (= [bk1]
         (lib/get-books-for-borrower br1 bks1))))
(s/conform :unq/bks
           (lib/get-books-for-borrower br1 bks1))

(deftest get-books-for-borrower-2-books-test
  (is (= [bk4 bk3]
         (lib/get-books-for-borrower br3 bks3))))
(s/conform :unq/bks
           (lib/get-books-for-borrower br3 bks3))

(deftest check-out-fail-checked-out-test
  (is (= bks1
         (lib/check-out "Borrower3" "Title1" brs2 bks1))))
(s/conform :unq/bks
           (lib/check-out "Borrower3" "Title1" brs2 bks1))

(deftest check-out-fail-bad-book-test
  (is (= bks1
         (lib/check-out "Borrower3" "NoTitle" brs2 bks1))))
(s/conform :unq/bks
           (lib/check-out "Borrower3" "NoTitle" brs2 bks1))

(deftest check-out-fail-bad-borrower-test
  (is (= bks1
         (lib/check-out "NoName" "Title1" brs2 bks1))))
(s/conform :unq/bks
           (lib/check-out "NoName" "Title1" brs2 bks1))

(deftest check-out-fail-over-limit-test
  (is (= bks1
         (lib/check-out "Borrower1" "Title2" brs2 bks1))))
(s/conform :unq/bks
           (lib/check-out "Borrower1" "Title2" brs2 bks1))

(deftest check-out-pass-test
  (is (= bks1
         (lib/check-out "Borrower2" "Title1" brs1 bks1))))
(s/conform :unq/bks
           (lib/check-out "Borrower2" "Title2" brs1 bks1))

(deftest check-in-pass-test
  (is (= bks4
         (lib/check-in "Title1" bks1))))
(s/conform :unq/bks
           (lib/check-in "Title1" bks1))

(deftest check-in-fail-checked-in-test
  (is (= bks1
         (lib/check-in "Title2" bks1))))
(s/conform :unq/bks
           (lib/check-in "Title1" bks1))

(deftest check-in-fail-bad-book-test
  (is (= bks1
         (lib/check-in "NoTitle" bks1))))
(s/conform :unq/bks
           (lib/check-in "NoTitle" bks1))

(deftest read-file-pass
  (let [maybe-s (tot/read-file-into-json-string "resources/borrowers-before.json")]
    (is (= [nil '({:max-books 10
                   :name      "Borrower10"}
                  {:max-books 9
                   :name      "Borrower9"})]
           (lib/json-string-to-list maybe-s)))))
(s/conform (s/or :is-json-brs (s/tuple nil? :unq/brs)
                 :is-json-bks (s/tuple nil? :unq/bks)
                 :is-error (s/tuple string? nil?))
           (let [maybe-s (tot/read-file-into-json-string "resources/borrowers-before.json")]
             (lib/json-string-to-list maybe-s)))

(deftest read-file-fail
  (let [maybe-s (tot/read-file-into-json-string "no-file.json")]
    (is (= ["no-file.json (No such file or directory)" nil]
           (lib/json-string-to-list maybe-s)))))
(s/conform (s/or :is-json-brs (s/tuple nil? :unq/brs)
                 :is-json-bks (s/tuple nil? :unq/bks)
                 :is-error (s/tuple string? nil?))
           (let [maybe-s (tot/read-file-into-json-string "no-file.json")]
             (lib/json-string-to-list maybe-s)))

(deftest json-parse-fail-test
  (let [maybe-s (tot/read-file-into-json-string "resources-test/bad-json.json")]
    (is (= ["JSON error (invalid array)" nil]
           (lib/json-string-to-list maybe-s)))))
(s/conform (s/or :is-json-brs (s/tuple nil? :unq/brs)
                 :is-json-bks (s/tuple nil? :unq/bks)
                 :is-error (s/tuple string? nil?))
           (let [maybe-s (tot/read-file-into-json-string "resources-test/bad-json.json")]
             (lib/json-string-to-list maybe-s)))

(deftest json-parse-pass-brs-test
  (let [maybe-s (lib/json-string-to-list [nil json-string-borrowers])]
    (is (= [nil brs1]
           maybe-s))))
(s/conform (s/or :is-json-brs (s/tuple nil? :unq/brs)
                 :is-json-bks (s/tuple nil? :unq/bks)
                 :is-error (s/tuple string? nil?))
           (lib/json-string-to-list [nil json-string-borrowers]))

(deftest json-parse-pass-bks-test
  (let [maybe-s (lib/json-string-to-list [nil json-string-books])]
    (is (= [nil bks1]
           maybe-s))))
(s/conform (s/or :is-json-brs (s/tuple nil? :unq/brs)
                 :is-json-bks (s/tuple nil? :unq/bks)
                 :is-error (s/tuple string? nil?))
           (lib/json-string-to-list [nil json-string-books]))

(deftest collection-to-json-string-test
  (is (= json-string-books
         (lib/collection-to-json-string bks5))))
(s/conform string?
           (lib/collection-to-json-string bks5))

(deftest library-to-string-test
  (is (= "Test Library: 2 books; 3 borrowers."
         (lib/library-to-string bks1 brs2))))
(s/conform string?
           (lib/library-to-string bks1 brs2))

(deftest status-to-string-test
  (is (= "\n--- Status Report of Test Library ---\n\nTest Library: 3 books; 3 borrowers.\nTitle1 by Author1; Checked out to Borrower1\nTitle2 by Author2; Available\nTitle3 by Author3; Checked out to Borrower3\n\nBorrower2 (2 books)\nBorrower1 (1 books)\nBorrower3 (3 books)\n\n--- End of Status Report ---\n"
         (lib/status-to-string bks2 brs2))))
(s/conform string?
           (lib/status-to-string bks2 brs2))
