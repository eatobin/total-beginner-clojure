(ns total.book
  (:require [total.borrower :as br]
            [clojure.data.json :as json]
            [malli.dev :as dev]
            [malli.dev.pretty :as pretty]))

(def =>title [:string {:min 1}])
(def =>author [:string {:min 1}])
(def =>maybe-borrower [:maybe br/=>borrower])
(def =>book [:map {:closed true} [:title =>title] [:author =>author] [:maybe-borrower =>maybe-borrower]])

;; sample usage:
(def =>get-title [:=> [:cat =>book] =>title])

(defn get-title
  "get the title of a book"
  {:malli/schema =>get-title}
  [book]
  (:title book))

(defn get-author
  "get the author of a book"
  {:malli/schema [:=> [:cat =>book] =>author]}
  [book]
  (:author book))

(defn get-borrower
  "get the maybe borrower of a book"
  {:malli/schema [:=> [:cat =>book] =>maybe-borrower]}
  [book]
  (:maybe-borrower book))

(defn set-borrower
  "set a borrower"
  {:malli/schema [:=> [:cat =>book =>maybe-borrower] =>book]}
  [book borrower]
  (assoc book :maybe-borrower borrower))

(defn- available-string
  "helper string if available"
  {:malli/schema [:=> [:cat =>book] :string]}
  [book]
  (let [borrower (get-borrower book)]
    (if (nil? borrower)
      "Available"
      (str
        "Checked out to "
        (br/get-name borrower)))))

(defn to-string
  "book to string"
  {:malli/schema [:=> [:cat =>book] :string]}
  [book]
  (str
    (get-title book)
    " by "
    (get-author book)
    "; "
    (available-string book)))

(defn- my-key-reader
  "string -> keyword"
  {:malli/schema [:=> [:cat [:string {:min 1}]] :keyword]}
  [string-key]
  (cond
    (= string-key "maxBooks") :max-books
    (= string-key "borrower") :maybe-borrower
    :else (keyword string-key)))

(defn- my-key-writer
  "keyword -> string"
  {:malli/schema [:=> [:cat :keyword] :string]}
  [keyword-key]
  (cond
    (= keyword-key :max-books) "maxBooks"
    (= keyword-key :maybe-borrower) "borrower"
    :else (name keyword-key)))

(defn book-json-string-to-book
  "create a book from a JSON string"
  {:malli/schema [:=> [:cat :string] =>book]}
  [book-string]
  (json/read-str book-string
                 :key-fn my-key-reader))

(defn book-to-json-string
  "create a JSON string from a book"
  {:malli/schema [:=> [:cat =>book] :string]}
  [book]
  (json/write-str book
                  :key-fn my-key-writer))

(dev/start! {:report (pretty/reporter)})
