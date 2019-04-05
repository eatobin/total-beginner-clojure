(ns total.library-test
  (:require [clojure.test :refer :all]
            [total.borrower :as br]
            [total.book :as bk]
            [total.library :as lib]
    ;[total-beginner.main :as mn]
            [clojure.spec.alpha :as s]))

(def br1 {::br/name "Borrower1" ::br/max-books 1})
(def br2 {::br/name "Borrower2" ::br/max-books 2})
(def br3 {::br/name "Borrower3" ::br/max-books 3})

(def brs1 (list br1 br2))
(def brs2 (list br2 br1 br3))

(def bk1 {::bk/title "Title1", ::bk/author "Author1", ::bk/maybe-borrower br1})
(def bk2 {::bk/title "Title2", ::bk/author "Author2", ::bk/maybe-borrower nil})
(def bk3 {::bk/title "Title3", ::bk/author "Author3", ::bk/maybe-borrower br3})
(def bk4 {::bk/title "Title4", ::bk/author "Author4", ::bk/maybe-borrower br3})

(def bks1 (list bk2 bk1))
(def bks2 (list bk1 bk2 bk3))
(def bks3 (list bk1 bk2 bk3 bk4))
(def bks4 (list bk2 {::bk/title "Title1", ::bk/author "Author1", ::bk/maybe-borrower nil}))
(def bks5 (list bk1 bk2))

(def json-string-borrowers-bad "[{\"name\"\"Borrower1\",\"max-books\":1},{\"name\":\"Borrower2\",\"max-books\":2}]")
(def json-string-borrowers "[{\"max-books\":2, \"name\":\"Borrower2\"},{\"name\":\"Borrower1\",\"max-books\":1}]")
(def json-string-books "[{\"total.book/title\":\"Title1\",\"total.book/author\":\"Author1\",\"total.book/maybe-borrower\":{\"total.borrower/name\":\"Borrower1\",\"total.borrower/max-books\":1}},{\"total.book/title\":\"Title2\",\"total.book/author\":\"Author2\",\"total.book/maybe-borrower\":null}]")

(deftest add-borrower-pass-test
  (is (= brs2
         (lib/add-item br3 brs1))))
(s/conform ::lib/brs
           (lib/add-item br3 brs1))

(deftest add-book-pass-test
  (is (= bks2
         (lib/add-item bk3 bks1))))
(s/conform ::lib/bks
           (lib/add-item bk3 bks1))

(deftest add-borrower-fail-test
  (is (= brs1
         (lib/add-item br2 brs1))))
(s/conform ::lib/brs
           (lib/add-item br2 brs1))

(deftest add-book-fail-test
  (is (= bks1
         (lib/add-item bk2 bks1))))
(s/conform ::lib/bks
           (lib/add-item bk2 bks1))

(deftest remove-book-pass-test
  (is (= bks1
         (lib/remove-book bk3 bks2))))
(s/conform ::lib/bks
           (lib/remove-book bk3 bks2))

(deftest remove-book-fail-test
  (is (= bks5
         (lib/remove-book bk3 bks1))))
(s/conform ::lib/bks
           (lib/remove-book bk3 bks1))

(deftest find-book-pass-test
  (is (= bk1
         (lib/find-item "Title1" bks2 bk/get-title))))
(s/conform (s/or :found-br ::br/borrower
                 :found-bk ::bk/book
                 :not-found nil?)
           (lib/find-item "Title1" bks2 bk/get-title))

(deftest find-book-fail-test
  (is (= nil
         (lib/find-item "Title11" bks2 bk/get-title))))
(s/conform (s/or :found-br ::br/borrower
                 :found-bk ::bk/book
                 :not-found nil?)
           (lib/find-item "Title11" bks2 bk/get-title))

(deftest find-borrower-pass-test
  (is (= br1
         (lib/find-item "Borrower1" brs2 br/get-name))))
(s/conform (s/or :found-br ::br/borrower
                 :found-bk ::bk/book
                 :not-found nil?)
           (lib/find-item "Borrower1" brs2 br/get-name))

(deftest find-borrower-fail-test
  (is (= nil
         (lib/find-item "Borrower11" brs1 br/get-name))))
(s/conform (s/or :found-br ::br/borrower
                 :found-bk ::bk/book
                 :not-found nil?)
           (lib/find-item "Borrower11" brs1 br/get-name))

(deftest get-books-for-borrower-0-books-test
  (is (= []
         (lib/get-books-for-borrower br2 bks1))))
(s/conform ::lib/bks
           (lib/get-books-for-borrower br2 bks1))

(deftest get-books-for-borrower-1-book-test
  (is (= [bk1]
         (lib/get-books-for-borrower br1 bks1))))
(s/conform ::lib/bks
           (lib/get-books-for-borrower br1 bks1))

(deftest get-books-for-borrower-2-books-test
  (is (= [bk4 bk3]
         (lib/get-books-for-borrower br3 bks3))))
(s/conform ::lib/bks
           (lib/get-books-for-borrower br3 bks3))

(deftest check-out-fail-checked-out-test
  (is (= bks1
         (lib/check-out "Borrower3" "Title1" brs2 bks1))))
(s/conform ::lib/bks
           (lib/check-out "Borrower3" "Title1" brs2 bks1))

(deftest check-out-fail-bad-book-test
  (is (= bks1
         (lib/check-out "Borrower3" "NoTitle" brs2 bks1))))
(s/conform ::lib/bks
           (lib/check-out "Borrower3" "NoTitle" brs2 bks1))

(deftest check-out-fail-bad-borrower-test
  (is (= bks1
         (lib/check-out "NoName" "Title1" brs2 bks1))))
(s/conform ::lib/bks
           (lib/check-out "NoName" "Title1" brs2 bks1))

(deftest check-out-fail-over-limit-test
  (is (= bks1
         (lib/check-out "Borrower1" "Title2" brs2 bks1))))
(s/conform ::lib/bks
           (lib/check-out "Borrower1" "Title2" brs2 bks1))

(deftest check-out-pass-test
  (is (= bks1
         (lib/check-out "Borrower2" "Title1" brs1 bks1))))
(s/conform ::lib/bks
           (lib/check-out "Borrower2" "Title2" brs1 bks1))

(deftest check-in-pass-test
  (is (= bks4
         (lib/check-in "Title1" bks1))))
(s/conform ::lib/bks
           (lib/check-in "Title1" bks1))

(deftest check-in-fail-checked-in-test
  (is (= bks1
         (lib/check-in "Title2" bks1))))
(s/conform ::lib/bks
           (lib/check-in "Title1" bks1))

(deftest check-in-fail-bad-book-test
  (is (= bks1
         (lib/check-in "NoTitle" bks1))))
(s/conform ::lib/bks
           (lib/check-in "NoTitle" bks1))

;(deftest read-file-pass
;  (let [s (mn/read-file-into-json-string "empty.json")]
;    (is (= '()
;           (lib/json-string-to-list s)))))
;(s/conform (s/or :is-json list?
;                 :is-error string?)
;           (let [s (mn/read-file-into-json-string "empty.json")]
;             (lib/json-string-to-list s)))
;
;(deftest read-file-fail
;  (let [s (mn/read-file-into-json-string "no-file.json")]
;    (is (= "File read error"
;           (lib/json-string-to-list s)))))
;(s/conform (s/or :is-json list?
;                 :is-error string?)
;           (let [s (mn/read-file-into-json-string "no-file.json")]
;             (lib/json-string-to-list s)))

(deftest json-parse-fail-test
  (is (= "JSON parse error"
         (lib/json-string-to-brs json-string-borrowers-bad))))
(s/conform (s/or :is-json ::lib/brs
                 :is-error string?)
           (lib/json-string-to-brs json-string-borrowers-bad))

(deftest json-parse-pass-brs-test
  (is (= brs1
         (lib/json-string-to-brs json-string-borrowers))))
(s/conform (s/or :is-json ::lib/brs
                 :is-error string?)
           (lib/json-string-to-brs json-string-borrowers))

;(deftest json-parse-pass-bks-test
;  (is (= bks1
;         (lib/json-string-to-bks json-string-books))))
;(s/conform (s/or :is-json (s/or :is-brs ::lib/brs
;                                :is-bks ::lib/bks)
;                 :is-error string?)
;           (lib/json-string-to-brs json-string-books))

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
