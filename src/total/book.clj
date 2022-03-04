(ns total.book
  (:require [total.domain :as dom]
            [total.borrower :as br]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as ostest]
            [malli.instrument :as mi]))

(def =>title [:string {:min 1}])
(def =>author [:string {:min 1}])
(def =>maybe-borrower [:maybe br/=>borrower])
(def =>book [:map {:closed true} [:title =>title] [:author =>author] [:maybe-borrower =>maybe-borrower]])

(defn get-title
  "get the title of a book"
  {:malli/schema [:=> [:cat =>book] =>title]}
  [book]
  (:title book))

(defn get-author [book]
  (:author book))
(s/fdef get-author
        :args (s/cat :book :unq/book)
        :ret ::dom/author)

(defn get-borrower [book]
  (:maybe-borrower book))
(s/fdef get-borrower
        :args (s/cat :book :unq/book)
        :ret ::dom/maybe-borrower)

(defn set-borrower [book borrower]
  (assoc book :maybe-borrower borrower))
(s/fdef set-borrower
        :args (s/cat :book :unq/book
                     :borrower ::dom/maybe-borrower)
        :ret :unq/book)

(defn- available-string [book]
  (let [borrower (get-borrower book)]
    (if (nil? borrower)
      "Available"
      (str
        "Checked out to "
        (br/get-name borrower)))))
(s/fdef available-string
        :args (s/cat :book :unq/book)
        :ret string?)

(defn to-string [book]
  (str
    (get-title book)
    " by "
    (get-author book)
    "; "
    (available-string book)))
(s/fdef to-string
        :args (s/cat :book :unq/book)
        :ret string?)

(defn- my-key-reader
  [key]
  (cond
    (= key "maxBooks") :max-books
    (= key "borrower") :maybe-borrower
    :else (keyword key)))

(defn- my-key-writer
  [key]
  (cond
    (= key :max-books) "maxBooks"
    (= key :maybe-borrower) "borrower"
    :else (name key)))

(defn book-json-string-to-book [book-string]
  (json/read-str book-string
                 :key-fn my-key-reader))
(s/fdef book-json-string-to-book
        :args (s/cat :book-string string?)
        :ret :unq/book)

(defn book-to-json-string [book]
  (json/write-str book
                  :key-fn my-key-writer))
(s/fdef book-to-json-string
        :args (s/cat :book :unq/book)
        :ret string?)

(ostest/instrument)
(mi/collect!)
(mi/instrument!)
