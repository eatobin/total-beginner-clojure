(ns total.total
  (:require [total.domain :as dom]
            [total.borrower :as br]
            [total.book :as bk]
            [total.library :as lib]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as ostest])
  (:gen-class))

(defn print-status [a-books a-borrowers]
  (println (lib/status-to-string (deref a-books) (deref a-borrowers))))

(defn read-file-into-json-string [file-path]
  (try
    [nil (slurp file-path)]
    (catch Exception e
      [(str (.getMessage e)) nil])))
(s/fdef read-file-into-json-string
        :args (s/cat :file-path string?)
        :ret (s/or :success (s/tuple nil? string?)
                   :failure (s/tuple string? nil?)))
(s/conform (s/or :success (s/tuple nil? string?)
                 :failure (s/tuple string? nil?))
           (read-file-into-json-string "resources-test/beatles.json"))
(s/conform (s/or :success (s/tuple nil? string?)
                 :failure (s/tuple string? nil?))
           (read-file-into-json-string "nope.json"))

(defn write-file-from-json-string [string file]
  (spit file string))

(defn new-a [a-books a-borrowers brs-file bks-file]
  (let [json-brs-str (read-file-into-json-string brs-file)
        json-bks-str (read-file-into-json-string bks-file)
        brs (lib/json-string-to-list json-brs-str dom/br-fields)
        bks (lib/json-string-to-list json-bks-str dom/bk-fields)]
    (if (or (= brs "File read error") (= brs "JSON parse error"))
      (println (str "\n" brs))
      (reset! a-borrowers brs))
    (if (or (= bks "File read error") (= bks "JSON parse error"))
      (println (str "\n" bks))
      (reset! a-books bks))
    (print-status a-books a-borrowers)))

(defn new-empty-a [a-books a-borrowers]
  (reset! a-books ())
  (reset! a-borrowers ())
  (print-status a-books a-borrowers))

(def json-borrowers-file-before "resources/borrowers-before.json")
(def json-borrowers-file-after "resources/borrowers-after.json")
(def json-books-file "resources/books-before.json")
(def json-borrowers-file-bad "resources/bad-borrowers.json")
(def empty-file "resources/empty.json")

(defn -main [& _]
  (let [a-borrowers (atom ())
        a-books (atom ())]
    (swap! a-borrowers (partial lib/add-item (br/make-borrower "Jim" 3)))
    (swap! a-borrowers (partial lib/add-item (br/make-borrower "Sue" 3)))
    (swap! a-books (partial lib/add-item (bk/make-book "War And Peace" "Tolstoy")))
    (swap! a-books (partial lib/add-item (bk/make-book "Great Expectations" "Dickens")))
    (println "\nJust created new library")
    (print-status a-books a-borrowers)

    (println "Check out War And Peace to Sue")
    (swap! a-books (partial lib/check-out "Sue" "War And Peace" (deref a-borrowers)))
    (print-status a-books a-borrowers)

    (println "Now check in War And Peace from Sue...")
    (swap! a-books (partial lib/check-in "War And Peace"))
    (println "...and check out Great Expectations to Jim")
    (swap! a-books (partial lib/check-out "Jim" "Great Expectations" (deref a-borrowers)))
    (print-status a-books a-borrowers)

    (println "Add Eric and The Cat In The Hat")
    (swap! a-borrowers (partial lib/add-item (br/make-borrower "Eric" 1)))
    (swap! a-books (partial lib/add-item (bk/make-book "The Cat In The Hat" "Dr. Seuss")))
    (println "Check Out Dr. Seuss to Eric")
    (swap! a-books (partial lib/check-out "Eric" "The Cat In The Hat" (deref a-borrowers)))
    (print-status a-books a-borrowers)

    (println "Now let's do some BAD stuff...\n")

    (println "Add a borrower that already exists (make-borrower \"Jim\" 3):")
    (swap! a-borrowers (partial lib/add-item (br/make-borrower "Jim" 3)))
    (println "No change to Test Library:")
    (print-status a-books a-borrowers)

    (println "Add a book that already exists (make-book \"War And Peace\" \"Tolstoy\" nil):")
    (swap! a-books (partial lib/add-item (bk/make-book "War And Peace" "Tolstoy")))
    (println "No change to Test Library:")
    (print-status a-books a-borrowers)

    (println "Check out a valid book to an invalid person (check-out \"JoJo\" \"War And Peace\" (deref a-borrowers)):")
    (swap! a-books (partial lib/check-out "JoJo" "War And Peace" (deref a-borrowers)))
    (println "No change to Test Library:")
    (print-status a-books a-borrowers)

    (println "Check out an invalid book to an valid person (check-out \"Sue\" \"Not A Book\" (deref a-borrowers)):")
    (swap! a-books (partial lib/check-out "Sue" "Not A Book" (deref a-borrowers)))
    (println "No change to Test Library:")
    (print-status a-books a-borrowers)

    (println "And... check in a book not checked out (check-in \"War And Peace\"):")
    (swap! a-books (partial lib/check-in "War And Peace"))
    (println "No change to Test Library:")
    (print-status a-books a-borrowers)

    (println "Okay... let's finish with some persistence. First clear the whole library:")
    (new-empty-a a-books a-borrowers)

    (println "Lets read in a new library from \"borrowers-before.json\" and \"books-before.json\":")
    (new-a a-books a-borrowers json-borrowers-file-before json-books-file)
    (println "Add... a new borrower:")
    (swap! a-borrowers (partial lib/add-item (br/make-borrower "BorrowerNew" 300)))
    (print-status a-books a-borrowers)

    (println "Save the revised borrowers to \"borrowers-after.json\"")
    (write-file-from-json-string (lib/collection-to-json-string (deref a-borrowers)) json-borrowers-file-after)

    (println "Clear the whole library again:")
    (new-empty-a a-books a-borrowers)

    (println "Then read in the revised library from \"borrowers-after.json\" and \"books-before.json\":")
    (new-a a-books a-borrowers json-borrowers-file-after json-books-file)

    (println "Last... delete the file \"borrowers-after.json\"")
    (io/delete-file json-borrowers-file-after)

    (println "Then try to make a library using the deleted \"borrowers-after.json\":")
    (new-a a-books a-borrowers json-borrowers-file-after json-books-file)

    (println "And if we read in a file with mal-formed json content... like \"bad-borrowers.json\":")
    (new-a a-books a-borrowers json-borrowers-file-bad json-books-file)

    (println "Or how about reading in an empty file... \"empty.json\":")
    (new-a a-books a-borrowers empty-file empty-file)

    (println "And... that's all...")
    (println "Thanks - bye!\n")))

(ostest/instrument)
