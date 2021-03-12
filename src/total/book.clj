(ns total.book
  (:require [total.domain :as dom]
            [total.borrower :as br]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as ostest]))

(defn get-title [book]
  (:title book))
(s/def get-title
  ::dom/extract-fn-bk-title)

;(defn get-author [book]
;  (:author book))
;(s/fdef get-author
;        :args (s/cat :book :unq/book)
;        :ret ::dom/author)
;
;(defn get-borrower [book]
;  (:maybe-borrower book))
;(s/fdef get-borrower
;        :args (s/cat :book :unq/book)
;        :ret ::dom/maybe-borrower)
;
;(defn set-borrower [book borrower]
;  (assoc book :maybe-borrower borrower))
;(s/fdef set-borrower
;        :args (s/cat :book :unq/book
;                     :borrower ::dom/maybe-borrower)
;        :ret :unq/book)
;
;(defn- available-string [book]
;  (let [borrower (get-borrower book)]
;    (if (nil? borrower)
;      "Available"
;      (str
;        "Checked out to "
;        (br/get-name borrower)))))
;(s/fdef available-string
;        :args (s/cat :book :unq/book)
;        :ret string?)
;
;(defn book-to-string [book]
;  (str
;    (get-title book)
;    " by "
;    (get-author book)
;    "; "
;    (available-string book)))
;(s/fdef book-to-string
;        :args (s/cat :book :unq/book)
;        :ret string?)
;
;def bookJsonStringToBook(bookString: String): Either[Error, Book] = decode[Book](bookString)
;
;def bookToJsonString(bk: Book): JsonString =
;bk.asJson.noSpaces
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

(ostest/instrument)
